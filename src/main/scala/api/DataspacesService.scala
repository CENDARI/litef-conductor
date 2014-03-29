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
import spray.http.HttpResponse
import java.sql.Timestamp

class DataspacesService(dataspaces: ActorRef)(implicit executionContext: ExecutionContext)
    extends CommonDirectives
{
    // Dataspaces and metadata
    def listDataspaces(since: Option[Timestamp], until: Option[Timestamp]) = complete {
        (dataspaces ? ListDataspaces(since, until)).mapTo[String]
    }

    def listDataspacesFromIterator(iteratorData: String) = complete {
        (dataspaces ? ListDataspacesFromIterator(iteratorData)).mapTo[String]
    }

    def getDataspaceMetadata(id: String) = complete {
        (dataspaces ? GetDataspaceMetadata(id)).mapTo[HttpResponse]
    }

    def getDataspaceMetadataItem(id: String, item: String) =
        complete {
            (dataspaces ? GetDataspaceMetadataItem(id, item)).mapTo[String]
        }

    // Defining the routes for different methods of the service
    val route =
        pathPrefix("dataspaces") {
            get {
                /*
                 * Getting the lists of results
                 */
                (path(PathEnd) & timeRestriction)   { listDataspaces } ~
                path("query" / "results" / Segment) { listDataspacesFromIterator }
            }
        }

}
