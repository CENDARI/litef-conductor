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
import spray.httpx.SprayJsonSupport._
import spray.json._
import DefaultJsonProtocol._
import MediaTypes._
import HttpCharsets._
import HttpMethods._
import HttpHeaders._

import common.Config.{ Ckan => CkanConfig }

import scala.concurrent.ExecutionContext.Implicits.global
import spray.http.HttpResponse
import java.sql.Timestamp
import ckan.{CkanGodInterface, DataspaceTable}
import spray.httpx.SprayJsonSupport._
import ckan.DataspaceJsonProtocol._
import ckan.ResourceJsonProtocol._
import CkanJsonProtocol._
import scala.slick.lifted.{Column, Query}
import spray.http.HttpHeaders.Location
import ckan.CkanGodInterface.IteratorData
import java.util.UUID

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

    /// Creates a dataspace
    case class CreateDataspace(
            val authorizationKey: String,
            val dataspace: DataspaceCreateWithId
          )

    /// Updates a dataspace
    case class UpdateDataspace(
            val authorizationKey: String,
            val dataspace: DataspaceUpdateWithId)

    /// Creates a new package in the dataspace
    case class CreatePackageInDataspace(
            val authorizationKey: String,
            val p: PackageCreateWithId)
}

class DataspaceActor
    extends Actor
    with dataapi.DefaultValues
{
    import DataspaceActor._
    import context.system

    def postRequest[T](action: String, data: T, authorizationKey: String)(implicit evidence: spray.httpx.marshalling.Marshaller[T]) =
        (IO(Http) ? (
            Post(CkanConfig.namespace + "action/" + action, data)~>addHeader("Authorization", authorizationKey)
        ))

    def receive: Receive = {
        /// Gets the list of resources modified in the specified time range
        case ListDataspaces(authorizationKey, since, until, start, count) =>
            CkanGodInterface.database withSession { implicit session: Session =>
                val (query, nextPage, currentPage) =
                    CkanGodInterface.listDataspacesQuery(authorizationKey, since, until, start, count)

                // Dataspaces do not support iterators thanks to CKAN //
                // "nextPage"    -> JsString(nextPage.map("/resources/query/results/" + _)    getOrElse ""),
                // "currentPage" -> JsString(currentPage.map("/resources/query/results/" + _) getOrElse ""),

                sender ! HttpResponse(status = StatusCodes.OK,
                                      entity = HttpEntity(ContentType(`application/json`, `UTF-8`),
                                                          JsObject("data" -> query.list.toJson).prettyPrint))
            }

        /// Decodes the iterator data and invokes ListDataspaces
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

        // Creates a new dataspace
        case CreateDataspace(authorizationKey, dataspace) => {
            val originalSender = sender

            // Passing the request to CKAN
            postRequest("organization_create", dataspace, authorizationKey)
                .mapTo[HttpResponse]
                .map { response => response.status match {
                    case StatusCodes.OK =>
                        // TODO: Try to read dataspace from (ugly) CKAN response, not from db
                        val createdDataspace = CkanGodInterface.getDataspace(authorizationKey, dataspace.id)
                        originalSender ! HttpResponse(status = StatusCodes.Created,
                                                     entity = HttpEntity(ContentType(`application/json`, `UTF-8`),
                                                                         createdDataspace.map { _.toJson.prettyPrint}.getOrElse {""}),
                                                     headers = List(Location(s"${common.Config.namespace}dataspaces/${dataspace.id}")))

                    case StatusCodes.BadRequest =>
                        originalSender ! HttpResponse(status = response.status, entity = response.entity)

                    case _ =>
                        originalSender ! HttpResponse(response.status, "Error creating dataspace!")
                }
            }
        }

        // Updates the dataspace
        case UpdateDataspace(authorizationKey, dataspace) => {
            val originalSender = sender

            // Passing the request to CKAN
            postRequest("organization_update", dataspace, authorizationKey)
                .mapTo[HttpResponse]
                .map { response => response.status match {
                    case StatusCodes.OK =>
                        // TODO: Try to read dataspace from (ugly) CKAN response, not from db
                        val updatedDataspace = CkanGodInterface.getDataspace(authorizationKey, dataspace.id)
                        originalSender ! HttpResponse(status = StatusCodes.OK,
                                                     entity = HttpEntity(ContentType(`application/json`, `UTF-8`),
                                                                         updatedDataspace.map { _.toJson.prettyPrint} getOrElse ""))

                    case StatusCodes.BadRequest =>
                        originalSender ! HttpResponse(status = response.status, entity = response.entity)

                    case _ =>
                        originalSender ! HttpResponse(response.status, "Error updating dataspace!")
                }
            }
        }

        // Creates a new package in the dataspace
        case CreatePackageInDataspace(authorizationKey, p) => {
            val originalSender = sender

            postRequest("package_create", p, authorizationKey)
                .mapTo[HttpResponse]
                .map { response => originalSender ! response}
        }

        case response: HttpResponse =>
            println(s"Sending the response back to the requester $response")

        case other =>
            println(s"Found an unknown thing: $other")
            sender ! other
    }

}
