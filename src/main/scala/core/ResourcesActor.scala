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

package core

import akka.actor.Actor
import akka.io.IO
import akka.pattern.ask
import scala.concurrent.duration._
import spray.can.Http
import spray.http._
import spray.httpx.RequestBuilding._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}
import spray.http.HttpResponse

object ResourcesActor {
    case class ListResources(val since: Option[Long], val until: Option[Long])
    case class GetResourceData(id: String)
    case class GetResourceMetadata(id: String)
    case class GetResourceMetadataItem(id: String, item: String)
    case class ListResourceAttachments(id: String, since: Option[Long], until: Option[Long])
    case class GetResourceAttachment(id: String, mimetype: String)
}

class ResourcesActor extends Actor {
    import ResourcesActor._
    import context.system

    implicit val timeout: akka.util.Timeout = 25.seconds

    val validCredentials = BasicHttpCredentials("cendariuser", "ck2n1f40f")

    def receive: Receive = {
        case ListResources(since, until) =>
            IO(Http) forward (
                Get("http://134.76.21.222/ckan/api/3/action/package_list") ~>
                    addCredentials(validCredentials)
            )

        case GetResourceMetadata(id) =>
            sender ! s"Resources Actor: Getting the resource $id metadata"

        case response: HttpResponse =>
            println(s"Sending the response back to the requester $response")

        case other =>
            println(s"Found an unknown thing: $other")
            sender ! other
    }

}
