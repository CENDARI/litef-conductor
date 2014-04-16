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

package api

import akka.actor.ActorRef
import akka.pattern.ask
import scala.concurrent.ExecutionContext
import spray.routing._
import core.ResourceActor._
import spray.http.HttpResponse
import java.sql.Timestamp
import core.Core

// Needed for implicit conversions, not unused:
import scala.concurrent.ExecutionContext.Implicits.global
import reflect.ClassTag


class ResourceService()(implicit executionContext: ExecutionContext)
    extends CommonDirectives
{
    // Resources and metadata
    // def listResources(since: Option[Timestamp], until: Option[Timestamp])(implicit authorizationKey: String) =
    //     complete {
    //         (Core.resourceActor ? ListResources(since, until)).mapTo[String]
    //     }
    //
    // def listResourcesFromIterator(iteratorData: String)(implicit authorizationKey: String) =
    //     complete {
    //         (Core.resourceActor ? ListResourcesFromIterator(iteratorData)).mapTo[String]
    //     }

    def getResourceMetadata(id: String)(implicit authorizationKey: String) =
        authorize(core.ckan.CkanGodInterface.isResourceAccessibleToUser(id.split('.').head, authorizationKey)) {
            complete {
                (Core.resourceActor ? GetResourceMetadata(id)).mapTo[HttpResponse]
            }
        }

    def getResourceData(id: String)(implicit authorizationKey: String) =
        authorize(core.ckan.CkanGodInterface.isResourceAccessibleToUser(id, authorizationKey)) {
            complete {
                (Core.resourceActor ? GetResourceData(id)).mapTo[HttpResponse]
            }
        }

    // def getResourceMetadataItem(id: String, item: String)(implicit authorizationKey: String) =
    //     if (item == "data")
    //         getResourceData(id)
    //     else complete {
    //         (Core.resourceActor ? GetResourceMetadataItem(id, item)).mapTo[String]
    //     }

    def test(argument: String)(implicit authorizationKey: String) =
        complete {
            argument + " " + authorizationKey
        }

    // Defining the routes for different methods of the service
    val route = headerValueByName("Authorization") { implicit authorizationKey =>
        pathPrefix("resources") {
            get {
                /*
                 * Nothing to see here
                 */
                path("test" / Segment)              { test } ~
                /*
                 * Getting the lists of results
                 */
                /*
                (path(PathEnd) & timeRestriction)   { listResources } ~
                path("query" / "results" / Segment) { listResourcesFromIterator } ~
                */
                /*
                 * Getting the specific result item
                 */
                path(Segment)                       { getResourceMetadata } ~
                path(Segment / "data")              { getResourceData }
                // path(Segment / "$metadata" / Segment) { getResourceMetadataItem }
            } ~
            put {
                complete { s"What to put?" }
            }
        }
    }
}
