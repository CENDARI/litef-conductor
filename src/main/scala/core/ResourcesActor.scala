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
import spray.can.Http
import spray.http._
import spray.httpx.RequestBuilding._

import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}
import spray.http.HttpResponse
import java.sql.Timestamp

object ResourcesActor {
    case class ListResources(val since: Option[Timestamp], val until: Option[Timestamp])
    case class GetResourceData(id: String)
    case class GetResourceMetadata(id: String)
    case class GetResourceMetadataItem(id: String, item: String)
    case class ListResourceAttachments(id: String, since: Option[Timestamp], until: Option[Timestamp])
    case class GetResourceAttachment(id: String, mimetype: String)
}

class ResourcesActor
    extends Actor
    with api.DefaultValues
{
    import ResourcesActor._
    import context.system

    val _config = ConfigFactory.load getConfig "litef.conductor.ckan"
    def config(key: String): String = _config getString key

    val validCredentials = BasicHttpCredentials(
        config("username"),
        config("password")
    )

    def receive: Receive = {
        case ListResources(since, until) =>
            IO(Http) forward (
                Get(config("namespace") + "action/group_list") ~>
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
