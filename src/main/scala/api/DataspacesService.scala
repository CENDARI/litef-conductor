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
import spray.json._
import core.DataspacesActor._
import core.ResourcesActor._
import spray.http.HttpResponse
import java.sql.Timestamp
import core.Core

class DataspacesService()(implicit executionContext: ExecutionContext)
    extends CommonDirectives
{
    // Dataspaces and metadata
    def listDataspaces(since: Option[Timestamp], until: Option[Timestamp])(implicit authorizationKey: String) = complete {
        (Core.dataspaces ? ListDataspaces(authorizationKey, since, until))
            .mapTo[String]
    }

    def listDataspacesFromIterator(iteratorData: String)(implicit authorizationKey: String) = complete {
        (Core.dataspaces ? ListDataspacesFromIterator(authorizationKey, iteratorData))
            .mapTo[String]
    }

    def getDataspaceMetadata(id: String)(implicit authorizationKey: String) = complete {
        (Core.dataspaces ? GetDataspaceMetadata(authorizationKey, id))
            .mapTo[HttpResponse]
    }

    def listDataspaceResources(id: String, since: Option[Timestamp], until: Option[Timestamp])(implicit authorizationKey: String) =
        authorize(core.ckan.CkanGodInterface.isDataspaceAccessibleToUser(id, authorizationKey)) {
            complete {
                (Core.resources ? ListDataspaceResources(id, since, until))
                .mapTo[String]
            }
        }

    def listDataspaceResourcesFromIterator(id: String, iteratorData: String)(implicit authorizationKey: String) =
        authorize(core.ckan.CkanGodInterface.isDataspaceAccessibleToUser(id, authorizationKey)) {
            complete {
                (Core.resources ? ListDataspaceResourcesFromIterator(id, iteratorData))
                .mapTo[String]
            }
        }

    // Defining the routes for different methods of the service
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
                path(Segment / "resources" / "query" / "results" / Segment) { listDataspaceResourcesFromIterator }
            }
        }
    }

}
