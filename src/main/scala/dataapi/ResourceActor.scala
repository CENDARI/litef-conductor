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
import common.Config

import scala.concurrent.ExecutionContext.Implicits.global
import spray.http.HttpResponse
import java.sql.Timestamp
import ckan.{CkanGodInterface, ResourceTable, DataspaceResourceTable}
import ckan.ResourceJsonProtocol._
import ckan.DataspaceResourceJsonProtocol._
import CkanJsonProtocol._
import spray.httpx.SprayJsonSupport._
import scala.slick.lifted.{Column, Query}
import spray.http.HttpHeaders.Location
import ckan.CkanGodInterface.IteratorData
import HttpCharsets._
import HttpMethods._
import HttpHeaders._

object ResourceActor {
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

    /// Gets the meta data for the the specified resource
    case class GetResourceMetadataRDF(id: String, format: String)

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

    case class UploadFile(
            val authorizationKey: String,
            val data: MultipartFormData,
            val key: String
        )

    case class CreateResourceMetadata(
            val authorizationKey: String,
            val dataspaceId: String,
            val resource: ResourceMetadataCreateWithId
        )

    case class UpdateResourceMetadata(
            val authorizationKey: String,
            val resourceId: String,
            val format: Option[String],
            val name: Option[String],
            val description: Option[String]
        )

    case class UpdateResourceMetadataAndUrl(
            val authorizationKey: String,
            val resource: ResourceMetadataUpdateWithId
        )
}

class ResourceActor
    extends Actor
    with dataapi.DefaultValues
{
    import ResourceActor._
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
                            resource map {
                                templates.html.resource(_).toString
                            } getOrElse {
                                templates.html.error(505, id).toString
                            }
                        } else {
                            resource map {
                                _.toJson.prettyPrint
                            } getOrElse {
                                ""
                            }
                        }
                    )
                )
            }

        case GetResourceMetadataRDF(request, format) =>
            CkanGodInterface.database withSession { implicit session: Session =>

                val mimetype = if (format == "xml") "application/rdf+xml" else "text/n3"

                println(s"################### searching for $request in $format as $mimetype")

                val content = conductor.ResourceAttachmentTable.query
                    .filter(_.resourceId === request)
                    .filter(_.format === mimetype)
                    .map(_.content)
                    .list.headOption

                val result = content.map { content =>
                    HttpResponse(
                        status  = StatusCodes.OK,
                        entity = HttpEntity(
                            ContentType(MediaType.custom(mimetype), `UTF-8`),
                            content.map(_.toString) getOrElse ""
                        )
                    )
                } getOrElse {
                    HttpResponse(
                        status  = StatusCodes.NotFound,
                        entity  = HttpEntity.Empty
                    )
                }

                sender ! result
            }

        /// Gets the data of the specified resource
        case GetResourceData(id) =>
            CkanGodInterface.database withSession { implicit session: Session =>

                val resource = CkanGodInterface.getResource(id)

                // TODO: File as response, not redirection

                val result = resource map { resource =>
                    HttpResponse(
                        status  = StatusCodes.MovedPermanently,
                        headers = Location(resource.url) :: Nil,
                        entity  = HttpEntity.Empty
                    )
                } getOrElse {
                    HttpResponse(
                        status  = StatusCodes.NotFound,
                        entity  = HttpEntity.Empty
                    )
                }

                sender ! result
            }

        /// Gets the resources for the specified dataspace
        case ListDataspaceResources(dataspaceId, since, until, start, count) =>
            CkanGodInterface.database withSession { implicit session: Session =>

                val (query, nextPage, currentPage) = CkanGodInterface.listDataspaceResourcesQuery(dataspaceId, since, until, start, count)

                val results = query.list

                sender ! (
                    if (results.size > 0)
                        JsObject(
                            "nextPage"    -> JsString(nextPage map (s"${Config.namespace}dataspaces/$dataspaceId/resources/query/results/" + _)    getOrElse ""),
                            "currentPage" -> JsString(currentPage map (s"${Config.namespace}dataspaces/$dataspaceId/resources/query/results/" + _) getOrElse ""),
                            "data"        -> results.toJson,
                            "end"         -> JsBoolean(false)
                        ).prettyPrint
                    else
                        JsObject(
                            "end"         -> JsBoolean(true)
                        ).prettyPrint
                )
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

        case UploadFile (authorizationKey, data, key) => {
            val originalSender = sender

            // TODO: Send to CKAN a form containing just file and key
            val keyField = BodyPart(key)
            val namedKeyField = keyField.copy(headers = `Content-Disposition`("form-data", Map("name" -> "key")) +: keyField.headers)
            val seqWithKey = data.fields :+ namedKeyField
            val dataWithKey = MultipartFormData(seqWithKey)

            (IO(Http) ? (Post(CkanConfig.storage + "upload_handle", dataWithKey)~>addHeader("Authorization", authorizationKey)))
            .mapTo[HttpResponse]
            .map { response => originalSender ! response }
        }
        case CreateResourceMetadata(authorizationKey, dataspaceId, resource) => {
            val originalSender = sender
            (IO(Http) ? (Post(CkanConfig.namespace + "action/resource_create", resource)~>addHeader("Authorization", authorizationKey)))
            .mapTo[HttpResponse]
            .map { response => response.status match {
                    case StatusCodes.OK =>
                        // TODO: Try to read resource from (ugly) CKAN response, not from db
                        val createdResource = CkanGodInterface.getResource(resource.id)
                        originalSender ! HttpResponse(status = StatusCodes.Created,
                                                      entity = HttpEntity(ContentType(`application/json`, `UTF-8`),
                                                                             createdResource.map { _.toJson.prettyPrint}.getOrElse {""}),
                                                         headers = List(Location(s"${common.Config.namespace}resources/${resource.id}")))
                    case _ => originalSender ! HttpResponse(response.status,
                                                            entity = HttpEntity(ContentType(`text/html`, `UTF-8`),
                                                                                "File uploaded to ${resource.url} \nError creating resource metadata!"))
                    }
            }
        }
        case UpdateResourceMetadata(authorizationKey, resourceId, format, name, description) => {
            val originalSender = sender
            CkanGodInterface.getResourceUrl(resourceId) match {
                case None =>
                    originalSender ! HttpResponse(StatusCodes.NotFound)
                case Some(url) =>
                    val resource = ResourceMetadataUpdateWithId(resourceId, name, description, format, url)
                    (IO(Http) ? (Post(CkanConfig.namespace + "action/resource_update", resource)~>addHeader("Authorization", authorizationKey)))
                    .mapTo[HttpResponse]
                    .map { response => response.status match {
                        case StatusCodes.OK =>
                            // TODO: Try to read resource from (ugly) CKAN response, not from db
                            val updatedResource = CkanGodInterface.getResource(resourceId)
                            originalSender ! HttpResponse(status = StatusCodes.OK,
                                                          entity = HttpEntity(ContentType(`application/json`, `UTF-8`),
                                                                                 updatedResource.map { _.toJson.prettyPrint}.getOrElse {""}))
                        case _ => originalSender ! HttpResponse(response.status, "Error updating resource!")}
                    }
            }
        }

        case UpdateResourceMetadataAndUrl(authorizationKey, resource) => {
            val originalSender = sender
            (IO(Http) ? (Post(CkanConfig.namespace + "action/resource_update", resource)~>addHeader("Authorization", authorizationKey)))
            .mapTo[HttpResponse]
            .map { response => response.status match {
                case StatusCodes.OK =>
                    // TODO: Try to read resource from (ugly) CKAN response, not from db
                    val updatedResource = CkanGodInterface.getResource(resource.id)
                    originalSender ! HttpResponse(status = StatusCodes.OK,
                                                  entity = HttpEntity(ContentType(`application/json`, `UTF-8`),
                                                                         updatedResource.map { _.toJson.prettyPrint}.getOrElse {""}))
                case _ => originalSender ! HttpResponse(response.status, "Error updating resource!")}
            }
        }

        case response: HttpResponse =>
            println(s"Sending the response back to the requester $response")

        case other =>
            println(s"Found an unknown thing: $other")
            sender ! other
    }

}
