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

import core.Core
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

import common.Config.{ Conductor => ConductorConfig }

import scala.concurrent.ExecutionContext.Implicits.global
import spray.http.HttpResponse
import java.sql.Timestamp
import ckan.{CkanGodInterface, DataspaceTable}
import ckan.DataspaceJsonProtocol._
import scala.slick.lifted.{Column, Query}
import spray.http.HttpHeaders.Location
import ckan.CkanGodInterface.database
import scala.concurrent.duration._
import scala.concurrent.Future
import scala.util.{Success, Failure}
import akka.event.Logging._
import akka.event.Logging

object DispatcherActor {
    /// Request for processing
    abstract class ProcessRequest(val resource: ckan.Resource)

    /// Request for the specified resource to be processed
    case class ProcessResource(override val resource: ckan.Resource) extends ProcessRequest(resource)

    /// Used by us to tell the caller that we have finished processing
    case class ResourceProcessingFinished(resource: ckan.Resource)

    /// Used by us to tell the caller that we failed to process the resource
    case class ResourceProcessingFailed(ex: RuntimeException)

    /// We have been asked to process another resource, but we haven't finished processing the current one
    case class AlreadyRunningException() extends RuntimeException

    /// A generic resource processing exception (a plugin failed)
    case class ResourceProcessingException(resource: ckan.Resource) extends RuntimeException

    /// We don't know how to access the resource
    case class ResourceNotProcessableException(resource: ckan.Resource) extends RuntimeException

}

/**
 * Handles processing of a single resource
 */
class DispatcherActor
    extends Actor
    with dataapi.DefaultValues
{
    import DispatcherActor._
    import context.system

    val log = Logging(context.system, this)

    // List of plugins we need to run on every resource
    lazy val plugins: Seq[ActorRef] = ConductorConfig.plugins
        .map(name =>
                system.actorOf(
                    Props.create(Class.forName(name).asInstanceOf[Class[Actor]]),
                    "plugins-actor-" + name)
        )

    // The current resource processing request
    var currentRequest: Option[ProcessRequest] = None
    // Plugins that still haven't finished processing the current resource
    var leftPlugins: Seq[ActorRef] = null


    /**
     * Starts processing the resource, calls the first plugin
     */
    def startProcessing() {
        log.info("Starting processing")
        leftPlugins = plugins
        startNextPlugin()
    }

    /**
     * Continues processing the resource, calls one of the remaining plugins
     */
    def startNextPlugin() {
        if (leftPlugins.size == 0) {
            log.info("No more plugins, sending the signal to the collector")
            Core.collectorActor ! ResourceProcessingFinished(currentRequest.get.resource)
            currentRequest = None

        } else {
            log.info("Starting the next plugin")
            leftPlugins.head ! currentRequest.get
            leftPlugins = leftPlugins.tail

        }
    }

    def receive: Receive = {
        case request @ ProcessResource(resource) =>
            log.info(s"Got the request to process $resource")

            if (currentRequest.isEmpty) {
                currentRequest = Some(request)
                startProcessing()
            } else {
                log.warning("We are already processing something!")

                sender ! AlreadyRunningException
            }

        case PluginActor.ProcessingFinished() =>
            startNextPlugin()

        case PluginActor.ProcessingFailed() =>
            startNextPlugin()
    }
}

