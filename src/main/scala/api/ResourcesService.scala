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
import scala.concurrent.ExecutionContext
import spray.routing._



class ResourcesService(resources: ActorRef)(implicit executionContext: ExecutionContext)
    extends Directives
{
    def listResources(since: Option[Long], until: Option[Long]) =
        complete { s"Returning resources from $since to $until" }

    def getResourceMetadata(id: String) =
        complete { s"Out resource: $id" }

    def getResourceMetadataItem(id: String, item: String) =
        complete { s"Out resource meta item: $id $item" }

    def listResourceAttachments(id: String, since: Option[Long], until: Option[Long]) =
        complete { s"Geting the resource attachments $id" }

    def getResourceAttachment(id: String, mimetype: String) =
        complete { s"Geting the resource attachment $id $mimetype" }

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
