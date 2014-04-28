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

object CollectorActor {
    /// Starts the processing of the next resource
    case class ProcessNext()

    /// Notifies the actor that the processing of the previous one has finished
    case class ResourceProcessed(resourceId: String, format: Option[String], modified: Timestamp)

    /// Notifies the actor that the processing of the previous one has finished
    case class ResourceProcessingFailed(resourceId: String, format: Option[String])

    /// Notifies the actor that the processing of the previous one has finished
    case class ResourceAttachmentProcessingFailed(resourceId: String, format: Option[String])

    /// Requests the update to the queue
    case class UpdateQueue()

    /// The queue has been updated
    case class QueueUpdateFinished()
}

class CollectorActor
    extends Actor
    with dataapi.DefaultValues
{
    import CollectorActor._
    import context.system

    override
    def preStart(): Unit = database withSession { implicit session: Session =>
        // ScheduledResourceTable.query.ddl.create
        // ResourceAttachmentTable.query.ddl.create
    }

    lazy val dispatcherActor = system.actorOf(Props[conductor.DispatcherActor], "dispatcher-actor")

    def processNextResource(resourceId: String, format: Option[String]) = Future {
        database withSession { implicit session: Session =>
            val resourceOption: Option[(String, String, String)] =
                if (format.isEmpty) {
                    ckan.ResourceTable.query
                        .where(_.id === resourceId)
                        .list
                        .headOption
                        .filter(_.url startsWith CkanConfig.urlStoragePrefix)
                        .flatMap { resource =>
                            val file = resource.url.replaceFirst(CkanConfig.urlStoragePrefix, CkanConfig.localStoragePrefix)

                            val fileSize = (new java.io.File(file)).length

                            if (fileSize > ConductorConfig.fileSizeLimit) {
                                None
                            } else {
                                Some((resource.id, resource.mimetype getOrElse "", io.Source.fromFile(file).mkString))
                            }
                        }

                } else {
                    ResourceAttachmentTable.query
                        .where(_.resourceId === resourceId)
                        .where(_.format === format.get)
                        .where(_.content.isNotNull)
                        .map(resource => (resource.resourceId, resource.format, resource.content.get))
                        .list
                        .headOption
                }

            if (resourceOption.isEmpty) {
                self ! ResourceProcessingFailed(resourceId, format)

            } else {
                // Actual processing is deferred to the dispatcher actor
                dispatcherActor ! DispatcherActor.Process.tupled(resourceOption.get)

                // self ! ResourceProcessed(resourceId, format)

            }
        }
    }

    def processNext() = database withSession { implicit session: Session =>
        val next = ScheduledResourceTable.query
            .where(_.scheduled)
            .take(1)
            .list
            .headOption

        if (next.isEmpty) {
            // If not, update the queue
            system.scheduler.scheduleOnce(60 seconds, self, UpdateQueue())

        } else {
            // If yes, process it
            val resource = next.get
            processNextResource(resource.resourceId, resource.format)
        }
    }

    def updateQueue() = database withSession { implicit session: Session =>
        // Find items whose lastProcessed time is earlier than
        // modification time of a resource and
        // set the lastProcessed time to null
        val lastUpdate = ScheduledResourceTable.query
            .where(!_.scheduled)
            .map(_.lastProcessed)
            .max

        val newResources = ckan.ResourceTable.query
            .where(_.modified >= lastUpdate)
            .sortBy(_.modified asc)
            .map(_.id)

        val count: Int = ScheduledResourceTable.query
            .where(_.format isNull)
            .where(_.resourceId in newResources)
            .map(_.scheduled)
            .update(true)

    }

    def resourceProcessed(resourceId: String, format: Option[String], modified: Timestamp) = database withSession { implicit session: Session =>
        val lastUpdate = ScheduledResourceTable.query
            .where(!_.scheduled)
            .map(row => (row.scheduled, row.lastProcessed))
            .update((false, modified))
    }

    def receive: Receive = {
        case ProcessNext() =>
            processNext

        case ResourceProcessed(resourceId, format, modified) =>
            resourceProcessed(resourceId, format, modified)
            processNext

        case UpdateQueue() =>
            updateQueue

        case QueueUpdateFinished() =>
            self ! ProcessNext()
    }

}

