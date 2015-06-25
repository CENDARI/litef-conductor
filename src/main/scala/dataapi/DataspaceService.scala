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
import dataapi.PackageActor._
import spray.http.HttpResponse
import java.sql.Timestamp
import core.Core
import DefaultJsonProtocol._
import CkanJsonProtocol._
//import common.Config.{ Ckan => CkanConfig }
import scala.util.{Success, Failure}
import spray.http.HttpHeaders._
import java.util.UUID
import MediaTypes._
import HttpCharsets._
import HttpMethods._
import HttpHeaders._
import spray.httpx.marshalling._
import java.text.Normalizer
import java.text.Normalizer.Form
import StateFilter._
import Visibility._
import FilterStringProtocol._

class DataspaceService()(implicit executionContext: ExecutionContext)
    extends CommonDirectives
{
    // Dataspaces and metadata
    def listDataspaces(since: Option[String], until: Option[String], state: StateFilter, visibility: Option[Visibility])(implicit authorizationKey: String) = {
        try
        {
          val _since = stringToTimestamp(since)
          val _until = stringToTimestamp(until)
          complete {
          (Core.dataspaceActor ? ListDataspaces(authorizationKey, _since, _until, state, visibility))
              .mapTo[HttpResponse]
          }
        }
        catch
        {
          case e: java.text.ParseException  => complete { HttpResponse(StatusCodes.BadRequest, "Invalid date format") }
        }
        
    }

//    def listDataspacesFromIterator(iteratorData: String)(implicit authorizationKey: String) = complete {
//        (Core.dataspaceActor ? ListDataspacesFromIterator(authorizationKey, iteratorData))
//            .mapTo[HttpResponse]
//    }

    def getDataspaceMetadata(id: String)(implicit authorizationKey: String) = 
        authorize(ckan.CkanGodInterface.isDataspaceAccessibleToUser(id, authorizationKey)) {
            complete {
                (Core.dataspaceActor ? GetDataspaceMetadata(id))
                .mapTo[HttpResponse]
            }
        }

     def listDataspaceResources(id: String, since: Option[String], until: Option[String], state: StateFilter)(implicit authorizationKey: String) =
        authorize(ckan.CkanGodInterface.isDataspaceAccessibleToUser(id, authorizationKey)) {
          try
          {
              val _since = stringToTimestamp(since)
              val _until = stringToTimestamp(until)
              complete {
                  (Core.resourceActor ? ListDataspaceResources(id, _since, _until, state))
                  .mapTo[HttpResponse]
              }

          }
          catch
          {
            case e: java.text.ParseException  => complete { HttpResponse(StatusCodes.BadRequest, "Invalid date format") }
          }
        
        }

    def listDataspaceResourcesFromIterator(id: String, iteratorData: String)(implicit authorizationKey: String) =
        authorize(ckan.CkanGodInterface.isDataspaceAccessibleToUser(id, authorizationKey)) {
            complete {
                (Core.resourceActor ? ListDataspaceResourcesFromIterator(id, iteratorData))
                .mapTo[HttpResponse]
            }
        }

    def listDataspacePackages(id: String, state: StateFilter) (implicit authorizationKey: String) = {
        authorize(ckan.CkanGodInterface.isDataspaceAccessibleToUser(id, authorizationKey)) {
            complete {
                (Core.packageActor ? ListDataspacePackages(id, state))
                .mapTo[HttpResponse]
            }
        }
    }
    def listDataspacePackagesFromIterator(id: String, iteratorData: String) (implicit authorizationKey: String) = {
        authorize(ckan.CkanGodInterface.isDataspaceAccessibleToUser(id, authorizationKey)) {
            complete {
                (Core.packageActor ? ListDataspacePackagesFromIterator(id, iteratorData))
                .mapTo[HttpResponse]
            }
        }
    }
    def createDataspace(dataspace: DataspaceCreate)(implicit authorizationKey: String) = complete {
        (Core.dataspaceActor ? CreateDataspace(authorizationKey,
                                               new DataspaceCreateWithId(UUID.randomUUID.toString
                                                                         ,dataspace.name
                                                                         ,dataspace.title
                                                                         ,dataspace.description
                                                                         ,dataspace.visibility)))
        .mapTo[HttpResponse]
    }

    def updateDataspace(id: String, dataspace: DataspaceUpdate) (implicit authorizationKey: String) = 
        authorize(ckan.CkanGodInterface.isDataspaceDeletableByUser(id, authorizationKey)) {
            complete {
                (Core.dataspaceActor ? UpdateDataspace(authorizationKey, DataspaceUpdateWithId(id, dataspace.title, dataspace.description, dataspace.visibility)))
                .mapTo[HttpResponse]
            }
        }

    // Resources are created using POST /resources
//    def createResourceInDataspace(id: String, file: FormFile, format: Option[String], name: Option[String],
//                                  description: Option[String], setId: Option[String]) (implicit authorizationKey: String) =
//        // TODO: Check if authorization can be left to CKAN API
//        // TODO: All registered users should be allowed to add resources to public dataspaces (?)
//        authorize(ckan.CkanGodInterface.isDataspaceModifiableByUser(id, authorizationKey)) {
//
//            var set = setId.getOrElse("")
//            // check if set exists in the dataspace
//            if (set != "" && !ckan.CkanGodInterface.isPackageInDataspace(id, set)) {
//                complete { HttpResponse(StatusCodes.NotFound, s"""Set with id "$set" not found in dataspace "$id"!""")}
//            }
//            else {
//                if (set == "") {
//                    // create set
//                    set = UUID.randomUUID().toString
//
//                    // TODO: Set resource and new set name/title to file.name if name is not specified
//                    val setTitle = name.getOrElse("Unnamed API dataset")
//
//                    onSuccess((Core.dataspaceActor ? CreatePackageInDataspace(authorizationKey, PackageCreateWithId(set, id, setTitle))).mapTo[HttpResponse]) {
//                        case x if x.status != StatusCodes.OK =>
//                            complete { HttpResponse(x.status, "Error creating set for new resource!") }
//                        case _ =>
//                            complete {
//                                (Core.resourceActor ? CreateResource(authorizationKey, UUID.randomUUID().toString, file, name, format, description, set))
//                                .mapTo[HttpResponse]
//                            }
//                    }
//                }
//                else complete {
//                    (Core.resourceActor ? CreateResource(authorizationKey, UUID.randomUUID().toString, file, name, format, description, set))
//                    .mapTo[HttpResponse]
//                }
//            }
//        }

    def deleteDataspace(id: String) (implicit authorizationKey: String) =
        authorize(ckan.CkanGodInterface.isDataspaceDeletableByUser(id, authorizationKey)) {
            complete {
                (Core.dataspaceActor ? DeleteDataspace(authorizationKey, id))
                .mapTo[HttpResponse]
            }
    }
//    def normalizeText(text: String)= {
//        Normalizer.normalize(text, Form.NFD)
//            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
//            .replaceAll("[^\\p{ASCII}]+","")
//            .replaceAll(" ", "")
//            .toLowerCase()
//    }

    // Resources are updated using PUT /resources
//    def updateResourceInDataspace(id: String, resourceId: String, file: FormFile, format: Option[String], name: Option[String], description: Option[String]) (implicit authorizationKey: String) =
//        authorize(ckan.CkanGodInterface.isDataspaceModifiableByUser(id, authorizationKey)) {
//            complete {
//                (Core.resourceActor ? UpdateResource(authorizationKey, resourceId, file, name, format, description))
//                .mapTo[HttpResponse]
//            }
//        }

    val route = headerValueByName("Authorization") { implicit authorizationKey =>
        pathPrefix("dataspaces") {
            get {
                /*
                 * Getting the list of dataspaces
                 */
                pathEndOrSingleSlash {
                    parameters('since.as[String] ?, 'until.as[String] ?, 'state.as[StateFilter] ? StateFilter.ACTIVE, 'visibility.as[Option[Visibility]]) { listDataspaces }
                } ~
                //path("query" / "results" / Segment) { listDataspacesFromIterator } ~
                /*
                 * Getting the dataspace meta data
                 */
                path(Segment) { getDataspaceMetadata } ~
                /**
                 * Getting the list of resources that belong to a dataspace
                 */
                (path(Segment / "resources" ~ PathEnd)) { id =>
                  parameters('since.as[String] ?, 'until.as[String] ?, 'state.as[StateFilter] ? StateFilter.ACTIVE) { (since, until, state) =>
                        listDataspaceResources(id, since, until, state)
                    }
                } ~
                path(Segment / "resources" / "query" / "results" / Rest) { listDataspaceResourcesFromIterator } ~
                /*
                 * Getting the list of sets that belong to a dataspace
                 */
                path(Segment / "sets") { id =>
                    parameters('state.as[StateFilter] ? StateFilter.ACTIVE) { state =>
                        listDataspacePackages(id, state)
                    }
                } ~
                path(Segment / "sets" / "query" / "results"/ Segment) { listDataspacePackagesFromIterator }
            }~
            post {
              /*
               * Creating new dataspace
               */
              pathEnd {
                  entity(as[DataspaceCreate]) { createDataspace }
              } 
//              /*
//               * Adding new resource to dataspace
//               */
//              (path(Segment / "resources")
//                    & formFields('file.as[FormFile])
//                    & formFields('format.as[Option[String]])
//                    & formFields('name.as[Option[String]])
//                    & formFields('description.as[Option[String]])
//                    & formFields('setId.as[Option[String]]))  { createResourceInDataspace }
            }~
            put {
              /*
               * Updating dataspace
               */
              (path(Segment) & entity(as[DataspaceUpdate])) { updateDataspace }
//              /*
//               * Updating resource
//               */
//              (path(Segment/"resources"/Segment)
//                    & formFields('file.as[FormFile])
//                    & formFields('format.as[Option[String]])
//                    & formFields('name.as[Option[String]])
//                    & formFields('description.as[Option[String]])) { updateResourceInDataspace }
            } ~
            delete {
                path(Segment)   { deleteDataspace }
            }
        }
    }

}
