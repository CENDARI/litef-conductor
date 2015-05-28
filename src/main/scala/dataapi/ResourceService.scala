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
import java.sql.Timestamp
import core.Core
import slick.driver.PostgresDriver.simple._

// Needed for implicit conversions, not unused:
import scala.concurrent.ExecutionContext.Implicits.global
import reflect.ClassTag


class ResourceService()(implicit executionContext: ExecutionContext)
    extends CommonDirectives
{
     // Resources and metadata
     def listResources(since: Option[Timestamp], until: Option[Timestamp])(implicit authorizationKey: String) =
        complete {
             (Core.resourceActor ? ListResources(since, until)).mapTo[HttpResponse]//.mapTo[String]
         }
    
     def listResourcesFromIterator(iteratorData: String)(implicit authorizationKey: String) =
         complete {
             (Core.resourceActor ? ListResourcesFromIterator(iteratorData)).mapTo[HttpResponse]
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
        authorize(ckan.CkanGodInterface.isResourceDeletableByUser(id, authorizationKey)) { //TODO: dodaj isResourceDeletableByUser
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
                path(Segment)                       { getResourceMetadata } ~
                path(Segment / "OLDDATA")           { getResourceData } ~
                path(Segment / "format" / Rest)     { getResourceAttachment } ~
                path(Segment / "rdf" / Segment)     { getResourceAttachmentRDF } ~
                path(Segment / "rdf")               { getResourceAttachmentRDF(_, "n3") } ~
                path(Segment / "text")              { getResourceAttachment(_, "text/plain") } ~
                path(Segment / "data")              { id =>
                    authorize(ckan.CkanGodInterface.isResourceDeletableByUser(id, authorizationKey)) {
                        val resource = ckan.CkanGodInterface.getResource(id)
                        print("MILAN" + listResources(None,None))
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
