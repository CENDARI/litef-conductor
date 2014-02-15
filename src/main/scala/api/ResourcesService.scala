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

import akka.actor.{Actor, ActorRef}
import akka.pattern.ask
import scala.concurrent.{Future, ExecutionContext}
import scala.concurrent.duration._
import spray.routing._
import core.ResourcesActor._
import spray.http.HttpResponse

// Needed for implicit conversions, not unused:
import scala.concurrent.ExecutionContext.Implicits.global
import reflect.ClassTag


class ResourcesService(resources: ActorRef)(implicit executionContext: ExecutionContext)
    extends Directives
{
    implicit val timeout: akka.util.Timeout = 25.seconds

    // Resources and metadata
    def listResources(since: Option[Long], until: Option[Long]) = complete {
        (resources ? ListResources(since, until)).mapTo[HttpResponse]
    }

    def getResourceMetadata(id: String) = complete {
        (resources ? GetResourceMetadata(id)).mapTo[String]
    }

    def getResourceData(id: String) = complete {
        (resources ? GetResourceData(id)).mapTo[String]
    }

    def getResourceMetadataItem(id: String, item: String) =
        if (item == "data")
            getResourceData(id)
        else complete {
            (resources ? GetResourceMetadataItem(id, item)).mapTo[String]
        }

    // Resource attachments
    def listResourceAttachments(id: String, since: Option[Long], until: Option[Long]) = complete {
        (resources ? ListResourceAttachments(id, since, until)).mapTo[String]
    }

    def getResourceAttachment(id: String, mimetype: String) = complete {
        (resources ? GetResourceAttachment(id, mimetype)).mapTo[String]
    }

    // Resource listing can be filtered on the modification time.
    val timeRestriction =
        parameter('since.as[Long]?) &
        parameter('until.as[Long]?)

    // Defining the routes for different methods of the service
    val route = pathPrefix("resources") {
        get {
            /*
             * Getting the lists of results
             */
            (path(PathEnd) & timeRestriction)                 { listResources } ~
            (path(Segment / "attachments") & timeRestriction) { listResourceAttachments } ~
            /*
             * Getting the specific result item
             */
            path(Segment)                           { getResourceMetadata } ~
            path(Segment / Segment)                 { getResourceMetadataItem } ~
            path(Segment / "attachments" / Segment) { getResourceAttachment }
        } ~
        put {
            complete { s"What to put?" }
        }
    }

}
