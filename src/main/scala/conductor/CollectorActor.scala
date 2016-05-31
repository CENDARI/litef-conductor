/*
 * Copyright (C) 2014 Ivan Cukic <ivan at mi.sanu.ac.rs>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package conductor

import slick.driver.PostgresDriver.simple._

import akka.actor._
import akka.io.IO
import akka.pattern.ask
import spray.can.Http
import spray.http._
import spray.httpx.RequestBuilding._
import spray.json._
import DefaultJsonProtocol._
import MediaTypes._
import HttpCharsets._

import common.Config.{ Ckan => CkanConfig, Conductor => ConductorConfig }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import spray.http.HttpResponse
import java.sql.Timestamp
import ckan.{CkanGodInterface, DataspaceTable}
import ckan.DataspaceJsonProtocol._
import scala.slick.lifted.{Column, Query}
import spray.http.HttpHeaders.Location
import ckan.CkanGodInterface.database
import scala.concurrent.duration._
import akka.event.Logging._
import akka.event.Logging
import org.foment.utils.Exceptions._

object CollectorActor {

    /// Starts the service
    case class Start()

    /// Starts the processing of the next resource
    case class ProcessNext()
}

/**
 * Manages the queue of resources that need to be processed
 */
class CollectorActor
    extends Actor
    with dataapi.DefaultValues
{
    import CollectorActor._
    import context.system

    val log = Logging(context.system, this)

    override
    def preStart(): Unit = database withSession { implicit session: Session =>
        // Enable it the database needs to be initialized
        // ProcessedResourceTable.query.ddl.create
        // ResourceAttachmentTable.query.ddl.create

        system.scheduler.scheduleOnce(5 seconds, self, Start())
    }

    lazy val dispatcherActor = system.actorOf(Props[conductor.DispatcherActor], "dispatcher-actor")

    /**
     * Starts the processing of the specified resource.
     * @param resource resource
     * @return nothing important
     */
    def processResource(resource: ckan.Resource) = Future {
        // log.info(s"Lets find the resource we want ${resource.id} / ${resource.url}")

        // If the resource is a valid candidate for processing, passing it to the dispatcher,
        // otherwise write a note to self that we have failed.
        if (resource.isProcessable) {
            val id = resource.id

            database.withSession { implicit session =>
                ResourceAttachmentTable.query.where { _.resourceId === id }.delete
                ProcessedResourceTable.query.where { _.id === id }.delete
            }

            // log.info("Sending the request to the dispatcher")
            dispatcherActor ! DispatcherActor.ProcessResource(resource)
        } else {
            // log.info("An error occured - unable to process the resource")
            self ! DispatcherActor.ResourceProcessingFailed(DispatcherActor.ResourceNotProcessableException(resource))
        }
    }

    /**
     * Starts the processing of the specified resource attachment.
     * @param attachment attachment
     * @return nothing important
     */
    def processAttachment(attachment: conductor.ResourceAttachment) = Future {
        dispatcherActor ! DispatcherActor.ProcessAttachment(attachment)
    }

    val disableDocumentProcessing = new java.io.File("/opt/litef/conductor:disable-document-processing");
    val enableGC = new java.io.File("/opt/litef/conductor:enable-gc");

    /**
     * Find the next resource, and start processing it
     * @return
     */
    def processNext() {
        // If we have a file that tells us to stop the processing,
        // listen to it. And try again after one minute
        if (disableDocumentProcessing.exists()) {
            system.scheduler.scheduleOnce(60 seconds, self, Start())

        } else {

            if (enableGC.exists()) {
                System.gc()
            }

            if (!scheduledResources.isEmpty) {
                processNextResource()

            } else if (!scheduledAttachments.isEmpty) {
                processNextAttachment()

            } else if (!processNextAttachment && !processNextResource) {
                system.scheduler.scheduleOnce(60 seconds, self, Start())
            }

        }
    }

    var scheduledAttachments: List[conductor.ResourceAttachment] = List() // null
    var scheduledResources: List[ckan.Resource] = List() // null

    def getMoreAttachments() = database withSession { implicit session: Session =>
        val nextQuery =
            conductor.ResourceAttachmentTable.query
                .filterNot(attachment =>
                    attachment.resourceId in ProcessedResourceTable.query
                        .filter { p =>
                            p.attachment === attachment.format &&
                            p.id === attachment.resourceId &&
                            p.lastProcessed <= attachment.modified
                        }
                        .map { _.id }
                )
            .take(1000)

        scheduledAttachments = nextQuery.list
        logger.info(s"Indexing collected attachments (scheduling): ${scheduledAttachments.size}")
    }

    def processNextAttachment(): Boolean = {
        if (scheduledAttachments.isEmpty) {
            getMoreAttachments()
        }

        if (scheduledAttachments.isEmpty) {
            false
        } else {
            val nextAttachment = scheduledAttachments.head
            scheduledAttachments = scheduledAttachments.tail
            // logger.info(s"Indexing collected attachment: ${nextAttachment.resourceId} ${nextAttachment.format}")
            processAttachment(nextAttachment)
            true
        }
    }

    def getMoreResources() = database withSession { implicit session: Session =>
        val nextQuery =
            ckan.ResourceTable.query
                .filter(_.modified.isNotNull)
                //.filter(_.state === "active")
                .filter(_.urlType === "upload")
                .filterNot(resource =>
                    resource.id in ProcessedResourceTable.query
                        .filter { p =>
                            p.attachment === "" &&
                            p.id === resource.id &&
                            p.lastProcessed >= resource.modified
                        }
                        .map { _.id }
                )
            .take(100)

        scheduledResources = nextQuery.list
        logger.info(s"Indexing collected resources (scheduling): ${scheduledResources.size}")
    }

    def processNextResource(): Boolean = {
        if (scheduledResources.isEmpty) {
            getMoreResources()
        }

        if (scheduledResources.isEmpty) {
            false
        } else {
            val nextResource = scheduledResources.head
            scheduledResources = scheduledResources.tail
            // logger.info(s"Indexing collected attachment: ${nextResource.resourceId} ${nextResource.format}")
            processResource(nextResource)
            true
        }
    }

    /**
     * Sets the specified resource's last processed timestamp
     * @param resource
     * @return
     */
    def markResourceAsProcessed(resource: ckan.Resource) = database withSession { implicit session: Session =>
        exceptionless {
            ProcessedResourceTable.query +=
                ProcessedResource(resource.id, resource.modified, None)
            None
        }

        ProcessedResourceTable.query
            .filter { r =>
                r.id === resource.id &&
                r.attachment.isNull
            }
            .map(_.lastProcessed)
            .update(resource.modified)
    }

    /**
     * Sets the specified resource's last processed timestamp
     * @param resource
     * @return
     */
    def markAttachmentAsProcessed(attachment: conductor.ResourceAttachment) = database withSession { implicit session: Session =>
        exceptionless {
            ProcessedResourceTable.query +=
                ProcessedResource(
                    attachment.resourceId,
                    Some(attachment.modified),
                    Some(attachment.format))
            None
        }

        ProcessedResourceTable.query
            .filter { r =>
                r.id === attachment.resourceId &&
                r.attachment === attachment.format
            }
            .map(_.lastProcessed)
            .update(Some(attachment.modified))
    }

    def receive: Receive = {
        // When the actor is started, start processing the next resource
        case Start() =>
            receive(ProcessNext())

        // Processing the next resource
        case ProcessNext() =>
            // log.info("Got the request to process the next resource")
            processNext

        // We got the notice that a resource processing was finished
        case DispatcherActor.ResourceProcessingFinished(resource) =>
            // log.info(s"The dispatcher said it has finished processing $resource")
            markResourceAsProcessed(resource)
            processNext

        // We got the notice that a resource processing was finished
        case DispatcherActor.AttachmentProcessingFinished(attachment) =>
            // log.info(s"The dispatcher said it has finished processing $resource")
            markAttachmentAsProcessed(attachment)
            processNext

        // Failed to process the resource, ignore and continue
        case DispatcherActor.ResourceProcessingFailed(ex) =>
            ex match {
                case DispatcherActor.ResourceProcessingException(resource) =>
                    // log.info(s"The dispatcher said it has failed to process $ex")
                    markResourceAsProcessed(resource)
                case _ =>
                    // nothing
            }

            processNext

        // Passing messages meant for the dispatcher:
        case msg =>
            // log.info("Passing to the dispatcherActor " + msg.toString)
            dispatcherActor ! msg
    }

}

