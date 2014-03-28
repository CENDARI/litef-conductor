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

import slick.driver.PostgresDriver.simple._

import akka.actor.Actor
import akka.io.IO
import akka.pattern.ask
import spray.can.Http
import spray.http._
import spray.httpx.RequestBuilding._

import common.Config.{ Ckan => CkanConfig }

import scala.concurrent.ExecutionContext.Implicits.global
import spray.http.HttpResponse
import java.sql.Timestamp
import core.ckan.{CkanInterface, DataspaceTable}
import scala.slick.lifted.{Column, Query}
import spray.http.HttpHeaders.Location

object DataspacesActor {
    /// Gets the list of resources modified in the specified time range
    case class ListDataspaces(val since: Option[Timestamp], val until: Option[Timestamp])

    /// Gets the meta data for the the specified resource
    case class GetDataspaceMetadata(id: String)

    /// Gets a specific meta-data item for the specified resource
    case class GetDataspaceMetadataItem(id: String, item: String)
}

class DataspacesActor
    extends Actor
    with api.DefaultValues
{
    import DataspacesActor._
    import context.system

    val validCredentials = BasicHttpCredentials(
        CkanConfig.httpUsername,
        CkanConfig.httpPassword
    )

    def receive: Receive = {
        /// Gets the list of resources modified in the specified time range
        case ListDataspaces(since, until) =>
            CkanInterface.database withSession { implicit session: Session =>
                sender ! CkanInterface.getDataspacesQuery(since, until)
                    .map(res => (res.id, res.title))
                    .take(10)
                    .list.map(res => res._1 + " " + res._2.get).mkString("\n")
            }

        /// Gets the meta data for the the specified resource
        case GetDataspaceMetadata(id) => IO(Http) forward {
            Get(CkanConfig.namespace + "action/resource_show?id=" + id) ~>
                addCredentials(validCredentials)
        }

        case response: HttpResponse =>
            println(s"Sending the response back to the requester $response")

        case other =>
            println(s"Found an unknown thing: $other")
            sender ! other
    }

}
