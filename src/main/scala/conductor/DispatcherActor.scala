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

import common.Config.{ Conductor => ConductorConfig }

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

object DispatcherActor {

    case class Process(resource: String, mimetype: String, content: String)

    case class ProcessingFinished()
}

class DispatcherActor
    extends Actor
    with dataapi.DefaultValues
{
    import DispatcherActor._
    import context.system

    lazy val clients: Seq[ActorRef] = ConductorConfig.plugins
        .map(name =>
                system.actorOf(
                    Props.create(Class.forName(name).asInstanceOf[Class[Actor]]),
                    "plugins-actor-" + name)
        )

    def receive: Receive = {
        case Process(resource, mimetype, content) =>
    }

}

