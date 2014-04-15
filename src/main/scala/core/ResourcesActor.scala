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
import spray.json._
import DefaultJsonProtocol._
import MediaTypes._
import HttpCharsets._

import common.Config.{ Ckan => CkanConfig }

import scala.concurrent.ExecutionContext.Implicits.global
import spray.http.HttpResponse
import java.sql.Timestamp
import core.ckan.{CkanGodInterface, ResourceTable, DataspaceResourceTable}
import core.ckan.ResourceJsonProtocol._
import core.ckan.DataspaceResourceJsonProtocol._
import scala.slick.lifted.{Column, Query}
import spray.http.HttpHeaders.Location
import core.ckan.CkanGodInterface.IteratorData

object ResourcesActor {
    /// Gets the list of resources modified in the specified time range
    // case class ListResources(
    //         val since: Option[Timestamp],
    //         val until: Option[Timestamp],
    //         val start: Int = 0,
    //         val count: Int = CkanGodInterface.queryResultDefaultLimit
    //     )

    /// Gets the next results for the iterator
    // case class ListResourcesFromIterator(val iterator: String)

    /// Gets the data of the specified resource
    case class GetResourceData(id: String)

    /// Gets the meta data for the the specified resource
    case class GetResourceMetadata(id: String)

    /// Gets the resources for the specified dataset
    case class ListDataspaceResources(
            val dataspaceId: String,
            val since: Option[Timestamp],
            val until: Option[Timestamp],
            val start: Int = 0,
            val count: Int = CkanGodInterface.queryResultDefaultLimit
        )

    /// Gets the resources for the specified dataset
    case class ListDataspaceResourcesFromIterator(
            val dataspaceId: String,
            val iterator: String
        )
}

class ResourcesActor
    extends Actor
    with api.DefaultValues
{
    import ResourcesActor._
    import context.system

    val validCredentials = BasicHttpCredentials(
        CkanConfig.httpUsername,
        CkanConfig.httpPassword
    )

    def receive: Receive = {
        /// Gets the list of resources modified in the specified time range
        // case ListResources(since, until, start, count) =>
        //     CkanGodInterface.database withSession { implicit session: Session =>
        //
        //         val (query, nextPage, currentPage) = CkanGodInterface.listResourcesQuery(since, until, start, count)
        //
        //         sender ! JsObject(
        //             "nextPage"    -> JsString(nextPage.map("/resources/query/results/" + _)    getOrElse ""),
        //             "currentPage" -> JsString(currentPage.map("/resources/query/results/" + _) getOrElse ""),
        //             "data"        -> query.list.toJson
        //         ).prettyPrint
        //
        //     }
        //
        // case ListResourcesFromIterator(iteratorData) =>
        //     val iterator = IteratorData.fromId(iteratorData).get
        //     receive(ListResources(
        //         Some(iterator.since),
        //         Some(iterator.until),
        //         iterator.start,
        //         iterator.count
        //     ))

        /// Gets the meta data for the the specified resource
        // case GetResourceMetadata(id) => IO(Http) forward {
        //     Get(CkanConfig.namespace + "action/resource_show?id=" + id) ~>
        //         addCredentials(validCredentials)
        // }

        case GetResourceMetadata(request) =>
            CkanGodInterface.database withSession { implicit session: Session =>

                val requestParts = request.split('.')

                val id = requestParts.head
                val format = if (requestParts.size == 2) requestParts(1) else "json"
                val mimetype = if (format == "html") `text/html` else `application/json`

                val resource = CkanGodInterface.getResource(id)
                sender ! HttpResponse(
                    status = StatusCodes.OK,
                    entity = HttpEntity(
                        ContentType(mimetype, `UTF-8`),
                        if (format == "html") {
                            resource.map {
                                templates.html.resource(_).toString
                            }.getOrElse {
                                templates.html.error(505, id).toString
                            }
                        } else {
                            resource.map {
                                _.toJson.toString
                            }.getOrElse {
                                ""
                            }
                        }
                    )
                )
            }

        /// Gets the data of the specified resource
        case GetResourceData(id) =>
            CkanGodInterface.database withSession { implicit session: Session =>

                val resource = CkanGodInterface.getResource(id)

                resource map { resource =>
                    HttpResponse(
                        status  = StatusCodes.MovedPermanently,
                        headers = Location(resource.url) :: Nil,
                        entity  = EmptyEntity
                    )
                } getOrElse {
                    HttpResponse(
                        status  = StatusCodes.NotFound,
                        entity  = EmptyEntity
                    )
                }
            }

        /// Gets the resources for the specified dataspace
        case ListDataspaceResources(dataspaceId, since, until, start, count) =>
            CkanGodInterface.database withSession { implicit session: Session =>

                val (query, nextPage, currentPage) = CkanGodInterface.listDataspaceResourcesQuery(dataspaceId, since, until, start, count)

                sender ! JsObject(
                    "nextPage"    -> JsString(nextPage.map(s"/dataspaces/$dataspaceId/resources/query/results/" + _)    getOrElse ""),
                    "currentPage" -> JsString(currentPage.map(s"/dataspaces/$dataspaceId/resources/query/results/" + _) getOrElse ""),
                    "data"        -> query.list.toJson
                ).prettyPrint
            }


        /// Gets the resources for the specified dataspace
        case ListDataspaceResourcesFromIterator(dataspaceId, iteratorData) =>
            val iterator = IteratorData.fromId(iteratorData).get
            receive(ListDataspaceResources(
                dataspaceId,
                Some(iterator.since),
                Some(iterator.until),
                iterator.start,
                iterator.count
            ))

        case response: HttpResponse =>
            println(s"Sending the response back to the requester $response")

        case other =>
            println(s"Found an unknown thing: $other")
            sender ! other
    }

}
