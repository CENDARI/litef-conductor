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

import akka.actor.ActorRef
import akka.pattern.ask
import scala.concurrent.ExecutionContext
import spray.routing._
import spray.json._
import spray.http._
import dataapi.DataspaceActor._
import dataapi.ResourceActor._
import spray.http.HttpResponse
import java.sql.Timestamp
import core.Core
import DefaultJsonProtocol._
import CkanJsonProtocol._
import common.Config.{ Ckan => CkanConfig }
import scala.util.{Success, Failure}
import spray.http.HttpHeaders._
import java.util.UUID
import MediaTypes._
import HttpCharsets._
import HttpMethods._
import HttpHeaders._
import spray.http.HttpHeaders._
import spray.httpx.marshalling._
import java.util.UUID
import java.text.Normalizer
import java.text.Normalizer.Form

class DataspaceService()(implicit executionContext: ExecutionContext)
    extends CommonDirectives
{
    // Dataspaces and metadata
    def listDataspaces(since: Option[Timestamp], until: Option[Timestamp])(implicit authorizationKey: String) = complete {
        (Core.dataspaceActor ? ListDataspaces(authorizationKey, since, until))
            .mapTo[String]
    }

    def listDataspacesFromIterator(iteratorData: String)(implicit authorizationKey: String) = complete {
        (Core.dataspaceActor ? ListDataspacesFromIterator(authorizationKey, iteratorData))
            .mapTo[String]
    }

    def getDataspaceMetadata(id: String)(implicit authorizationKey: String) = complete {
        (Core.dataspaceActor ? GetDataspaceMetadata(authorizationKey, id))
            .mapTo[HttpResponse]
    }

    def listDataspaceResources(id: String, since: Option[Timestamp], until: Option[Timestamp])(implicit authorizationKey: String) =
        authorize(ckan.CkanGodInterface.isDataspaceAccessibleToUser(id, authorizationKey)) {
            complete {
                (Core.resourceActor ? ListDataspaceResources(id, since, until))
                .mapTo[String]
            }
        }

    def listDataspaceResourcesFromIterator(id: String, iteratorData: String)(implicit authorizationKey: String) =
        authorize(ckan.CkanGodInterface.isDataspaceAccessibleToUser(id, authorizationKey)) {
            complete {
                (Core.resourceActor ? ListDataspaceResourcesFromIterator(id, iteratorData))
                .mapTo[String]
            }
        }

    def createDataspace(dataspace: DataspaceCreate)(implicit authorizationKey: String) = complete {
        (Core.dataspaceActor ? CreateDataspace(authorizationKey,
                                               new DataspaceCreateWithId(UUID.randomUUID.toString
                                                                         ,dataspace.name
                                                                         ,dataspace.title
                                                                         ,dataspace.description)))
        .mapTo[HttpResponse]
    }

    def updateDataspace(id: String, dataspace: DataspaceUpdate) (implicit authorizationKey: String) = complete {
        (Core.dataspaceActor ? UpdateDataspace(authorizationKey, DataspaceUpdateWithId(id, dataspace.name, dataspace.title, dataspace.description)))
        .mapTo[HttpResponse]
    }

    def createResourceInDataspace(id: String, data: MultipartFormData, file: FormFile, format: Option[String], name: Option[String],
                                  description: Option[String], setId: Option[String]) (implicit authorizationKey: String) =
        // Authorization is not left to CKAN because its FileStorage API returns
        // 200 status + html page containing "not authorized" message among lots of other stuff
        // TODO: All registered users should be allowed to add resources to public dataspaces (?)
        authorize(ckan.CkanGodInterface.isDataspaceModifiableByUser(id, authorizationKey)) {
            val set = setId getOrElse ""
            if (set != "" && !ckan.CkanGodInterface.isPackageInDataspace(id, set))
                complete { HttpResponse(status = StatusCodes.NotFound, entity = HttpEntity(ContentType(`text/html`), s"Set with id $set not found in dataspace $id"))} // TODO: CKAN sends 404, which status should be used?
            else {
                val key = createFileKey(file.name.getOrElse("unnamed"))
                val fileUrl = CkanConfig.storage + "f/" + key
                onSuccess((Core.resourceActor ? UploadFile (authorizationKey, data, key)).mapTo[HttpResponse]) {
                    case x if x.status == StatusCodes.OK =>
                        createResourceMetadata(authorizationKey, id, name, description, format, set, fileUrl)
                    case x =>
                        complete { HttpResponse(x.status, "Error uploading file!") }
                }
            }
        }

    def createResourceMetadata(authorizationKey: String, id: String, name: Option[String], description: Option[String], format: Option[String], set: String, url: String) = {
        if(set == ""){
                val newSetId = UUID.randomUUID().toString
                onSuccess((Core.dataspaceActor ? CreatePackageInDataspace(authorizationKey, PackageCreateWithId(newSetId, id))).mapTo[HttpResponse]) {
                    case x if x.status == StatusCodes.OK =>
                        onSuccess((Core.resourceActor ? CreateResourceMetadata(authorizationKey, id,
                                                                               ResourceMetadataCreateWithId(UUID.randomUUID().toString, name, description, format, newSetId, url)))
                                  .mapTo[HttpResponse]) { case y => complete { y } }
                    case x =>  complete { HttpResponse(x.status,
                                           entity = HttpEntity(ContentType(`text/html`, `UTF-8`),
                                                                            s"File uploaded to $url \nError creating new set for resource!")) }
                }
        }
        else {
            onSuccess((Core.resourceActor ? CreateResourceMetadata(authorizationKey, id, ResourceMetadataCreateWithId(UUID.randomUUID().toString, name, description, format, set, url)))
                      .mapTo[HttpResponse]) { case x => complete { x } }
        }
    }

    def createFileKey(filename: String)= {
        val timestampStr = new Timestamp(System.currentTimeMillis()).toString.replace(":","").replace(" ", "")
        val formattedFilename = normalizeText(filename)
        timestampStr + "/" + formattedFilename
    }

    def normalizeText(text: String)= {
        Normalizer.normalize(text, Form.NFD)
            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
            .replaceAll("[^\\p{ASCII}]+","")
            .replaceAll(" ", "")
            .toLowerCase()
    }

    def updateResourceInDataspace(id: String, resourceId: String, data: MultipartFormData, file: Option[FormFile], format: Option[String], name: Option[String],
                                  description: Option[String]) (implicit authorizationKey: String) =
        authorize(ckan.CkanGodInterface.isDataspaceModifiableByUser(id, authorizationKey)) {
            file match  {
                case None =>
                    // Update only resource metadata
                    complete {
                        (Core.resourceActor ? UpdateResourceMetadata(authorizationKey, resourceId, format, name, description))
                        .mapTo[HttpResponse]
                    }
                case Some(f) =>
                    val key = createFileKey(f.name.getOrElse("unnamed"))
                    val fileUrl = CkanConfig.storage + "f/" + key
                    onSuccess((Core.resourceActor ? UploadFile (authorizationKey, data, key)).mapTo[HttpResponse]) {
                        case x if x.status == StatusCodes.OK =>
                            updateResourceMetadata(authorizationKey, resourceId, name, description, format, fileUrl)
                        case x =>
                            complete { HttpResponse(x.status, "Error uploading file!") }
                    }
            }
        }

    def updateResourceMetadata(authorizationKey: String, resourceId: String, name: Option[String], description: Option[String], format: Option[String], url: String) = complete {
        (Core.resourceActor ? UpdateResourceMetadataAndUrl(authorizationKey, ResourceMetadataUpdateWithId(resourceId, name, description, format, url)))
         .mapTo[HttpResponse]
    }
    val route = headerValueByName("Authorization") { implicit authorizationKey =>
        pathPrefix("dataspaces") {
            get {
                /*
                 * Getting the list of dataspaces
                 */
                (path(PathEnd) & timeRestriction)   { listDataspaces } ~
                (path(PathEnd) & timeRestriction)   { listDataspaces } ~
                path("query" / "results" / Segment) { listDataspacesFromIterator } ~
                /*
                 * Getting the dataspace meta data
                 */
                path(Segment) { getDataspaceMetadata } ~
                /**
                 * Getting the list of resources that belong to a dataspace
                 */
                (path(Segment / "resources" ~ PathEnd) & timeRestriction)   { listDataspaceResources } ~
                path(Segment / "resources" / "query" / "results" / Rest) { listDataspaceResourcesFromIterator }
            }~
            post {
              /*
               * Creating new dataspace
               */
              (pathEnd & entity(as[DataspaceCreate])) { createDataspace } ~
              /*
               * Adding new resource to dataspace
               */
              (path(Segment / "resources") & entity(as[MultipartFormData])
                    & formFields('file.as[FormFile])
                    & formFields('format.as[Option[String]])
                    & formFields('name.as[Option[String]])
                    & formFields('description.as[Option[String]])
                    & formFields('set_id.as[Option[String]]))  { createResourceInDataspace }
            }~
            put {
              /*
               * Updating dataspace
               */
              (path(Segment) & entity(as[DataspaceUpdate])) { updateDataspace } ~
              /*
               * Updating resource
               */
              (path(Segment/"resources"/Segment) & entity(as[MultipartFormData])
                    & formFields('file.as[Option[FormFile]])
                    & formFields('format.as[Option[String]])
                    & formFields('name.as[Option[String]])
                    & formFields('description.as[Option[String]])) { updateResourceInDataspace }
            }
        }
    }

}
