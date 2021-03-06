/*
 * Copyright (C) 2014 Ivan Cukic <ivan at mi.sanu.ac.rs>
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
import java.util.UUID

import common.Config.{ Ckan => CkanConfig }
import common.Config

import scala.concurrent.ExecutionContext.Implicits.global
import java.sql.Timestamp
import ckan.{CkanGodInterface, ResourceTable, DataspaceResourceTable}
import ckan.ResourceJsonProtocol._
import ckan.DataspaceResourceJsonProtocol._
import CkanJsonProtocol._
import spray.httpx.SprayJsonSupport._
import scala.slick.lifted.{Column, Query}
import ckan.IteratorData
import HttpMethods._
import HttpHeaders._
import akka.event.Logging._
import akka.event.Logging
import StateFilter._

import conductor.ResourceAttachmentUtil._

object ResourceActor {
    /// Gets the list of resources modified in the specified time range
     case class ListResources(
            val authorizationKey:   String,
            val since:              Option[Timestamp],
            val until:              Option[Timestamp],
            val state:              StateFilter,
            val start:              Int = 0,
            val count:              Int = CkanGodInterface.queryResultDefaultLimit
         )

    /// Gets the next results for the iterator
    case class ListResourcesFromIterator(
        val authorizationKey: String,
        val iterator: String
    )

    /// Gets the data of the specified resource
    case class GetResourceData(id: String)

    /// Gets the meta data for the the specified resource
    case class GetResourceMetadata(id: String)

    /// Gets the meta data for the the specified resource
    case class GetResourceMetadataAttachment(id: String, format: String)

    /// Gets the resources for the specified dataspace
    case class ListDataspaceResources(
            val dataspaceId: String,
            val since: Option[Timestamp],
            val until: Option[Timestamp],
            val state: StateFilter,
            val start: Int = 0,
            val count: Int = CkanGodInterface.queryResultDefaultLimit
        )

    /// Gets the resources for the specified dataspace
    case class ListDataspaceResourcesFromIterator(
            val dataspaceId: String,
            val iterator: String
        )

    /// Gets the resources for the specified package
    case class ListPackageResources(
            val packageId: String,
            val since: Option[Timestamp],
            val until: Option[Timestamp],
            val state: StateFilter,
            val start: Int = 0,
            val count: Int = CkanGodInterface.queryResultDefaultLimit
    )

    /// Gets the resources for the specified package
    case class ListPackageResourcesFromIterator(
            val packageId: String,
            val iterator: String
    )

    case class CreateResource(
            val authorizationKey: String,
            val file: FormFile,
            val name: Option[String],
            val format: Option[String],
            val description: Option[String],
            val packageId: String
        )

    case class UpdateResource(
            val authorizationKey: String,
            val id: String,
            val upload: FormFile,
            val name: Option[String],
            val format: Option[String],
            val description: Option[String]
        )
    case class DeleteResource(
            val authorizationKey: String,
            val id: String
        )

}

class ResourceActor
    extends Actor
    with dataapi.DefaultValues
{
    import ResourceActor._
    import context.system

    val log = Logging(context.system, this)

    lazy val logger = org.slf4j.LoggerFactory getLogger getClass

    def receive: Receive = {
        /// Gets the list of resources modified in the specified time range
        case ListResources(authorizationKey, since, until, state, start, count) =>

            CkanGodInterface.database withSession { implicit session: Session =>

                val (query, nextPage, currentPage) = CkanGodInterface.listResourcesQuery(authorizationKey, since, until, state, start, count)

                val resources = query.list
                val results =
                    if (resources.size > 0)
                        JsObject(
                            "nextPage"    -> JsString(nextPage map (s"${Config.namespace}resources/query/results/" + _)    getOrElse ""),
                            "currentPage" -> JsString(currentPage map (s"${Config.namespace}resources/query/results/" + _) getOrElse ""),
                            "data"        -> resources.toJson,
                            "end"         -> JsBoolean(false)
                        ).prettyPrint
                    else
                        JsObject(
                            "end"         -> JsBoolean(true)
                        ).prettyPrint

                sender ! HttpResponse(status = StatusCodes.OK,
                                      entity = HttpEntity(ContentType(`application/json`, `UTF-8`), results))
            }

        case ListResourcesFromIterator(authorizationKey, iteratorData) =>
            val iterator = IteratorData.fromId(iteratorData).get
            receive(ListResources(
                    authorizationKey,
                    Some(iterator.since),
                    Some(iterator.until),
                    iterator.state,
                    iterator.start,
                    iterator.count
                ))

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

        case GetResourceMetadataAttachment(request, mimetype) =>
            CkanGodInterface.database withSession { implicit session: Session =>

                // TODO: More security checks
                // println(s"################### searching for $request in $format as $mimetype")

                // Checking whether we have the attachment in the database
                val resourceId = conductor.ResourceAttachmentTable.query
                    .filter(_.resourceId === request)
                    .filter(_.format === mimetype)
                    .map(_.resourceId)
                    .list.headOption

                val result = resourceId.map { id =>
                    val filePath = conductor.ResourceAttachment(id, mimetype).localPath
                    val content = scala.io.Source.fromFile(filePath).mkString

                    HttpResponse(
                        status  = StatusCodes.OK,
                        entity = HttpEntity(
                            ContentType(MediaType.custom(mimetype), `UTF-8`),
                            content
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
                        headers = Location(resource.accessLink) :: Nil,
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
        case ListDataspaceResources(dataspaceId, since, until, state, start, count) =>
            CkanGodInterface.database withSession { implicit session: Session =>

                val (query, nextPage, currentPage) = CkanGodInterface.listDataspaceResourcesQuery(dataspaceId, since, until, state, start, count)

                val resources = query.list
                val results =
                    if (resources.size > 0)
                        JsObject(
                            "nextPage"    -> JsString(nextPage map (s"${Config.namespace}dataspaces/$dataspaceId/resources/query/results/" + _)    getOrElse ""),
                            "currentPage" -> JsString(currentPage map (s"${Config.namespace}dataspaces/$dataspaceId/resources/query/results/" + _) getOrElse ""),
                            "data"        -> resources.toJson,
                            "end"         -> JsBoolean(false)
                        ).prettyPrint
                    else
                        JsObject(
                            "end"         -> JsBoolean(true)
                        ).prettyPrint

                sender ! HttpResponse(status = StatusCodes.OK,
                                      entity = HttpEntity(ContentType(`application/json`, `UTF-8`), results))
            }

        /// Gets the resources for the specified dataspace
        case ListDataspaceResourcesFromIterator(dataspaceId, iteratorData) =>
            val iterator = IteratorData.fromId(iteratorData).get
            receive(ListDataspaceResources(
                dataspaceId,
                Some(iterator.since),
                Some(iterator.until),
                iterator.state,
                iterator.start,
                iterator.count
            ))

        /// Gets the resources for the specified package
        case ListPackageResources(packageId, since, until, state, start, count) =>
            CkanGodInterface.database withSession { implicit session: Session =>

                val (query, nextPage, currentPage) = CkanGodInterface.listPackageResourcesQuery(packageId, since, until, state, start, count)

                val resources = query.list
                val results =
                    if (resources.size > 0)
                        JsObject(
                            "nextPage"    -> JsString(nextPage map (s"${Config.namespace}sets/$packageId/resources/query/results/" + _)    getOrElse ""),
                            "currentPage" -> JsString(currentPage map (s"${Config.namespace}sets/$packageId/resources/query/results/" + _) getOrElse ""),
                            "data"        -> resources.toJson,
                            "end"         -> JsBoolean(false)
                        ).prettyPrint
                    else
                        JsObject(
                            "end"         -> JsBoolean(true)
                        ).prettyPrint

                sender ! HttpResponse(status = StatusCodes.OK,
                                      entity = HttpEntity(ContentType(`application/json`, `UTF-8`), results))
            }

        /// Gets the resources for the specified package
        case ListPackageResourcesFromIterator(packageId, iteratorData) =>
            val iterator = IteratorData.fromId(iteratorData).get
            receive(ListPackageResources(
                packageId,
                Some(iterator.since),
                Some(iterator.until),
                iterator.state,
                iterator.start,
                iterator.count
            ))

        case CreateResource(authorizationKey, file, name, format, description, packageId) => {
            val originalSender = sender

            val id = UUID.randomUUID().toString

            var fields = Seq[BodyPart]()

            val fieldId = BodyPart(id)
            val namedFieldId = fieldId.copy(headers = `Content-Disposition`("form-data", Map("name" -> "id")) +: fieldId.headers)
            fields = fields :+ namedFieldId

            val fieldUpload = BodyPart(file.entity)
            val namedFieldUpload = fieldUpload.copy(headers = `Content-Disposition`("form-data", Map("filename" -> file.name.get, "name" -> "upload")) +: fieldUpload.headers)
            fields = fields :+ namedFieldUpload

            val title = name match {
                case Some(s)    => s
                case None       => file.name.get
            }

            val fieldName = BodyPart(title)
            val namedFieldName = fieldName.copy(headers = `Content-Disposition`("form-data", Map("name" -> "name")) +: fieldName.headers)
            fields = fields :+ namedFieldName

            for (f <- format) {
                val fieldFormat = BodyPart(f)
                val namedFieldFormat = fieldFormat.copy(headers = `Content-Disposition`("form-data", Map("name" -> "format")) +: fieldFormat.headers)
                fields = fields :+ namedFieldFormat
            }

            for (d <- description) {
                val fieldDescription = BodyPart(d)
                val namedFieldDescription = fieldDescription.copy(headers = `Content-Disposition`("form-data", Map("name" -> "description")) +: fieldDescription.headers)
                fields = fields :+ namedFieldDescription
            }

            val fieldPackageId = BodyPart(packageId)
            val namedFieldPackageId = fieldPackageId.copy(headers = `Content-Disposition`("form-data", Map("name" -> "package_id")) +: fieldPackageId.headers)
            fields = fields :+ namedFieldPackageId

            val resource = MultipartFormData(fields)

            (IO(Http) ? (Post(CkanConfig.namespace + "action/resource_create", resource)~>addHeader("Authorization", authorizationKey)))
            .mapTo[HttpResponse]
            .map { response => response.status match {
                    case StatusCodes.OK =>
                        // TODO: Try to read resource from (ugly) CKAN response, not from db
                        val createdResource = CkanGodInterface.getResource(id)
                        createdResource match {
                            case Some(cr) =>
                                originalSender ! HttpResponse(status = StatusCodes.Created,
                                                              entity = HttpEntity(ContentType(`application/json`, `UTF-8`), cr.toJson.prettyPrint),
                                                              headers = List(Location(s"${Config.namespace}resources/$id")))
                            case None => originalSender ! HttpResponse(StatusCodes.InternalServerError, "Error reading newly created resource metadata from the database")

                        }

                    case _ =>
                        logger info s"Error creating resource: $response"
                        originalSender ! HttpResponse(response.status, "Error creating resource!")
                        //originalSender ! response
                    }
            }
        }

        case UpdateResource(authorizationKey, id, file, name, format, description) => {
            val originalSender = sender

            var fields = Seq[BodyPart]()

            val fieldId = BodyPart(id)
            val namedFieldId = fieldId.copy(headers = `Content-Disposition`("form-data", Map("name" -> "id")) +: fieldId.headers)
            fields = fields :+ namedFieldId

            val fieldUpload = BodyPart(file.entity)
            val namedFieldUpload = fieldUpload.copy(headers = `Content-Disposition`("form-data", Map("filename" -> file.name.get, "name" -> "upload")) +: fieldUpload.headers)
            fields = fields :+ namedFieldUpload
            //fields = fields :+ BodyPart("upload", upload)

            val title = name match {
                case Some(s)    => s
                case None       => file.name.get
            }
            val fieldName = BodyPart(title)
            val namedFieldName = fieldName.copy(headers = `Content-Disposition`("form-data", Map("name" -> "name")) +: fieldName.headers)
            fields = fields :+ namedFieldName

            for (f <- format) {
                val fieldFormat = BodyPart(f)
                val namedFieldFormat = fieldFormat.copy(headers = `Content-Disposition`("form-data", Map("name" -> "format")) +: fieldFormat.headers)
                fields = fields :+ namedFieldFormat
            }

            for (d <- description) {
                val fieldDescription = BodyPart(d)
                val namedFieldDescription = fieldDescription.copy(headers = `Content-Disposition`("form-data", Map("name" -> "description")) +: fieldDescription.headers)
                fields = fields :+ namedFieldDescription
            }

            val resource = MultipartFormData(fields)

            (IO(Http) ? (Post(CkanConfig.namespace + "action/resource_update", resource)~>addHeader("Authorization", authorizationKey)))
            .mapTo[HttpResponse]
            .map { response => response.status match {
                case StatusCodes.OK =>
                    // TODO: Try to read resource from (ugly) CKAN response, not from db
                    val updatedResource = CkanGodInterface.getResource(id)

                    updatedResource match {
                        case Some(ur)   => originalSender ! HttpResponse(status = StatusCodes.OK,
                                                                         entity = HttpEntity(ContentType(`application/json`, `UTF-8`), ur.toJson.prettyPrint))
                        case None       => originalSender ! HttpResponse(StatusCodes.InternalServerError, "The resources is updated, but its metadata cannot be read from the database")
                    }

                case _ =>
                    logger info s"Error updating resource: $response"
                    originalSender ! HttpResponse(response.status, s"""Error updating resource with id "$id"!""")
                }
            }
        }

        case DeleteResource(authorizationKey, id) => {
            val originalSender = sender

            (IO(Http) ? (Post(CkanConfig.namespace + "action/resource_delete", HttpEntity(`application/json`, """{ "id": """"+ id + """"}""" ))~>addHeader("Authorization", authorizationKey)))
            .mapTo[HttpResponse]
            .map { response => response.status match {
                case StatusCodes.OK =>
                    /*val deletedResource = CkanGodInterface.getResource(id)
                    originalSender ! HttpResponse(status = StatusCodes.OK,
                                                  entity = HttpEntity(ContentType(`application/json`, `UTF-8`),
                                                                         deletedResource.map { _.toJson.prettyPrint}.getOrElse {""}))*/
                     originalSender ! HttpResponse(StatusCodes.NoContent)

                case StatusCodes.Forbidden => originalSender ! HttpResponse(response.status, "The supplied authentication is not authorized to access this resource")
                case _ => originalSender ! HttpResponse(response.status, s"""Error deleting resource "$id"!""" + response)}
            }
        }

    case response: HttpResponse =>
        logger info s"Sending the response back to the requester ..." // $response")

    case other =>
        logger info s"Found an unknown thing: $other"
        sender ! other
    }

}
