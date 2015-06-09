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
import dataapi.ResourceActor._
import spray.http.HttpResponse
import spray.http.StatusCodes
import java.sql.Timestamp
import core.Core
import slick.driver.PostgresDriver.simple._
import StateFilter._
import StateFilterProtocol._

// Needed for implicit conversions, not unused:
import scala.concurrent.ExecutionContext.Implicits.global
import reflect.ClassTag


class ResourceService()(implicit executionContext: ExecutionContext)
    extends CommonDirectives
{
      //Resources and metadata
      def listResources(dataspaceId: Option[String], setId: Option[String], since: Option[String], until: Option[String], state: StateFilter)(implicit authorizationKey: String) =
        try
        {
            val _since = stringToTimestamp(since)
            val _until = stringToTimestamp(until)
            
            // TODO: return 404 if the dataspace/package with the specified ids do not exist
            (dataspaceId, setId) match {
                case (Some(did), Some(sid)) =>
                    if(ckan.CkanGodInterface.isPackageInDataspace(sid, did))
                        complete {
                            (Core.resourceActor ? ListPackageResources(sid, _since, _until, state)).mapTo[HttpResponse]
                        }
                    else 
                        complete {
                            HttpResponse(StatusCodes.NotFound, s"Package with id ${sid} does not exist in the dataspace with id ${did}")
                        }
                case (Some(did), None) =>
                    authorize(ckan.CkanGodInterface.isDataspaceAccessibleToUser(did, authorizationKey)) {
                        complete {
                            (Core.resourceActor ? ListDataspaceResources(did, _since, _until, state)).mapTo[HttpResponse]
                        }   
                    }
                case (None, Some(sid)) =>
                    authorize(ckan.CkanGodInterface.isPackageAccessibleToUser(sid, authorizationKey)) {
                        complete {
                            (Core.resourceActor ? ListPackageResources(sid, _since, _until, state)).mapTo[HttpResponse]
                        }
                    }
                case (None, None) =>
                    authorize(ckan.CkanGodInterface.isRegisteredUser(authorizationKey)) {
                        complete {
                            (Core.resourceActor ? ListResources(authorizationKey, _since, _until, state)).mapTo[HttpResponse]
                        }
                    }
            }
            
        }
        catch
        {
          case e: java.text.ParseException  => complete { HttpResponse(StatusCodes.BadRequest, "Invalid date format") }
        }
    
      def listResourcesFromIterator(iteratorData: String)(implicit authorizationKey: String) =
          complete {
              (Core.resourceActor ? ListResourcesFromIterator(authorizationKey, iteratorData)).mapTo[HttpResponse]
          }

    def getResourceMetadata(id: String)(implicit authorizationKey: String) =
        authorize(ckan.CkanGodInterface.isResourceAccessibleToUser(id.split('.').head, authorizationKey)) {
            complete {
                (Core.resourceActor ? GetResourceMetadata(id)).mapTo[HttpResponse]
            }
        }

    def getResourceAttachment(id: String, mimetype: String)(implicit authorizationKey: String) =
        authorize(ckan.CkanGodInterface.isResourceAccessibleToUser(id.split('.').head, authorizationKey)) {
            complete {
                (Core.resourceActor ? GetResourceMetadataAttachment(id, mimetype)).mapTo[HttpResponse]
            }
        }

    def getResourceAttachmentRDF(id: String, format: String)(implicit authorizationKey: String) =
        getResourceAttachment(id, if (format == "xml") "application/rdf+xml" else "text/n3")

    def getResourceData(id: String)(implicit authorizationKey: String) =
        authorize(ckan.CkanGodInterface.isResourceAccessibleToUser(id, authorizationKey)) {
            complete {
                (Core.resourceActor ? GetResourceData(id)).mapTo[HttpResponse]
            }
        }

    def deleteResource(id: String)(implicit authorizationKey: String) = {
        authorize(ckan.CkanGodInterface.isResourceDeletableByUser(id, authorizationKey)) {
          complete {
              (Core.resourceActor ? DeleteResource(authorizationKey, id))
              .mapTo[HttpResponse]
          }
       }
    }

    // def getResourceMetadataItem(id: String, item: String)(implicit authorizationKey: String) =
    //     if (item == "data")
    //         getResourceData(id)
    //     else complete {
    //         (Core.resourceActor ? GetResourceMetadataItem(id, item)).mapTo[String]
    //     }
    //
    implicit def actorRefFactory = core.Core.system
    implicit def settings = spray.routing.RoutingSettings.default

    lazy val logger = org.slf4j.LoggerFactory getLogger getClass

    // Defining the routes for different methods of the service
    val route = headerValueByName("Authorization") { implicit authorizationKey =>
        pathPrefix("resources") {
            get {
                pathEnd {
                    parameters('dataspaceId.as[String] ?,
                               'setId.as[String] ?,
                               'since.as[String] ?, 
                               'until.as[String] ?, 
                               'state.as[StateFilter] ? StateFilter.ACTIVE) 
                    { listResources }
                } ~
                path("query" / "results" / Segment) { listResourcesFromIterator } ~
                path(Segment)                       { getResourceMetadata } ~
                path(Segment / "OLDDATA")           { getResourceData } ~
                path(Segment / "format" / Rest)     { getResourceAttachment } ~
                path(Segment / "rdf" / Segment)     { getResourceAttachmentRDF } ~
                path(Segment / "rdf")               { getResourceAttachmentRDF(_, "n3") } ~
                path(Segment / "text")              { getResourceAttachment(_, "text/plain") } ~
                path(Segment / "data")              { id =>
                    authorize(ckan.CkanGodInterface.isResourceAccessibleToUser(id, authorizationKey)) {
                        val resource = ckan.CkanGodInterface.getResource(id)
                        
                        resource map { resource => 
                            logger info s"REQ RES ${resource.id} -> ${resource.localPath}"
                            getFromFile(resource.localPath)
                        } getOrElse {
                            // TODO: Make this work properly 
                            getFromFile("/error505")
                        }
                    }
                }
            } ~
            put {
                complete { s"What to put?" }
            } ~
            delete {
                path(Segment)   { deleteResource }
            }
        }
    }
}
