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

package dataapi

import slick.driver.PostgresDriver.simple._

import akka.actor.Actor
import akka.io.IO
import akka.pattern.ask
import spray.can.Http
import spray.http._
import spray.httpx.RequestBuilding._
import spray.json._
import DefaultJsonProtocol._
import MediaTypes._
import HttpCharsets._

import common.Config.{ Ckan => CkanConfig }

import scala.concurrent.ExecutionContext.Implicits.global
import spray.http.HttpResponse
import java.sql.Timestamp
import core.ckan.{CkanGodInterface, DataspaceTable}
import core.ckan.DataspaceJsonProtocol._
import scala.slick.lifted.{Column, Query}
import spray.http.HttpHeaders.Location
import core.ckan.CkanGodInterface.IteratorData

object DataspaceActor {
    /// Gets the list of resources modified in the specified time range
    case class ListDataspaces(
            val authorizationKey: String,
            val since: Option[Timestamp],
            val until: Option[Timestamp],
            val start: Int = 0,
            val count: Int = CkanGodInterface.queryResultDefaultLimit
        )

    /// Gets the next results for the iterator
    case class ListDataspacesFromIterator(
            val authorizationKey: String,
            val iterator: String
        )

    /// Gets the meta data for the the specified resource
    case class GetDataspaceMetadata(
            val authorizationKey: String,
            val id: String)
}

class DataspaceActor
    extends Actor
    with dataapi.DefaultValues
{
    import DataspaceActor._
    import context.system

    val validCredentials = BasicHttpCredentials(
        CkanConfig.httpUsername,
        CkanConfig.httpPassword
    )

    def receive: Receive = {
        /// Gets the list of resources modified in the specified time range
        case ListDataspaces(authorizationKey, since, until, start, count) =>
            CkanGodInterface.database withSession { implicit session: Session =>
                val (query, nextPage, currentPage) =
                    CkanGodInterface.listDataspacesQuery(authorizationKey, since, until, start, count)

                sender ! JsObject(
                    // Dataspaces do not support iterators thanks to CKAN //
                    // "nextPage"    -> JsString(nextPage.map("/resources/query/results/" + _)    getOrElse ""),
                    // "currentPage" -> JsString(currentPage.map("/resources/query/results/" + _) getOrElse ""),
                    "data"        -> query.list.toJson
                ).prettyPrint
            }

        case ListDataspacesFromIterator(authorizationKey, iteratorData) =>
            val iterator = IteratorData.fromId(iteratorData).get
            receive(ListDataspaces(
                authorizationKey,
                Some(iterator.since),
                Some(iterator.until),
                iterator.start,
                iterator.count
            ))

        /// Gets the meta data for the the specified resource
        case GetDataspaceMetadata(authorizationKey, request) =>
            CkanGodInterface.database withSession { implicit session: Session =>

                val requestParts = request.split('.')

                val id = requestParts.head
                val format = if (requestParts.size == 2) requestParts(1) else "json"
                val mimetype = if (format == "html") `text/html` else `application/json`

                val dataspace = CkanGodInterface.getDataspace(authorizationKey, id)

                sender ! HttpResponse(
                    status = StatusCodes.OK,
                    entity = HttpEntity(
                        ContentType(mimetype, `UTF-8`),
                        if (format == "html") {
                            dataspace.map {
                                templates.html.dataspace(_).toString
                            }.getOrElse {
                                templates.html.error(403, id).toString
                            }
                        } else {
                            dataspace.map {
                                _.toJson.prettyPrint
                            }.getOrElse {
                                ""
                            }
                        }
                    )
                )
            }


        case response: HttpResponse =>
            println(s"Sending the response back to the requester $response")

        case other =>
            println(s"Found an unknown thing: $other")
            sender ! other
    }

}
