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

object CollectorActor {

    case class Start()

    /// Starts the processing of the next resource
    case class ProcessNext()

    // /// Requests the update to the queue
    // case class UpdateQueue()
    //
    // /// The queue has been updated
    // case class QueueUpdateFinished()
}

class CollectorActor
    extends Actor
    with dataapi.DefaultValues
{
    import CollectorActor._
    import context.system

    val log = Logging(context.system, this)

    override
    def preStart(): Unit = database withSession { implicit session: Session =>
        // ProcessedResourceTable.query.ddl.create
        // ResourceAttachmentTable.query.ddl.create

        system.scheduler.scheduleOnce(5 seconds, self, Start())
    }

    lazy val dispatcherActor = system.actorOf(Props[conductor.DispatcherActor], "dispatcher-actor")

    def processResource(resource: ckan.Resource) = Future {
        log.info(s"Lets find the resource we want ${resource.id} / ${resource.url}")

        if (resource.isProcessable) {
            log.info("Sending the request to the dispatcher")
            dispatcherActor ! DispatcherActor.ProcessResource(resource)
        } else {
            log.info("An error occured - unable to process the resource")
            self ! DispatcherActor.ResourceProcessingFailed(DispatcherActor.ResourceNotProcessableException(resource))
        }
    }

    def processNext() = database withSession { implicit session: Session =>

        log.info("Processing the next resource:")

        val nextQuery =
            ckan.ResourceTable.query
                .filter(_.modified.isNotNull)
                .filter(_.url startsWith CkanConfig.urlStoragePrefix)
                .filterNot(resource =>
                    resource.id in ProcessedResourceTable.query
                        .filter{ p => resource.id === p.id && p.lastProcessed <= resource.modified }
                        .map(_.id)
                )
            .take(1)

        val next = nextQuery
            .list
            .headOption

        if (next.isEmpty) {
            // If not, update the queue
            log.info("The queue is empty, waiting for 60 seconds...")
            system.scheduler.scheduleOnce(60 seconds, self, Start())

        } else {
            // If yes, process it
            val resource = next.get
            log.info(s"Processing resource: ${resource.id} / ${resource.url}")
            processResource(resource)
        }
    }

    def markResourceAsProcessed(resource: ckan.Resource) = database withSession { implicit session: Session =>
        ProcessedResourceTable.query
            .filter(r => r.id === resource.id)
            .delete
        ProcessedResourceTable.query += ProcessedResource(resource.id, resource.modified)
    }

    def receive: Receive = {
        case Start() =>
            receive(ProcessNext())

        case ProcessNext() =>
            log.info("Got the request to process the next resource")
            processNext

        case DispatcherActor.ResourceProcessingFinished(resource) =>
            log.info(s"The dispatcher said it has finished processing $resource")
            markResourceAsProcessed(resource)
            processNext

        case DispatcherActor.ResourceProcessingFailed(ex) =>
            ex match {
                case DispatcherActor.ResourceProcessingException(resource) =>
                    log.info(s"The dispatcher said it has failed to process $ex")
                    markResourceAsProcessed(resource)
                case _ =>
                    // nothing
            }

            processNext

        // Messages meant for the dispatcher:
        case msg =>
            log.info("Passing to the dispatcherActor " + msg.toString)
            dispatcherActor ! msg
    }

}

