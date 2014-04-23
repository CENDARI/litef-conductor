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

package conductor

import slick.driver.PostgresDriver.simple._
import java.sql.Timestamp
import spray.json._

// ResourceAttachments
case class ResourceAttachments(
    resourceId     : String,
    format         : String,
    created        : Timestamp,
    modified       : Timestamp,
    content        : Option[String]
)

class ResourceAttachmentsTable(tag: Tag)
    extends Table[ResourceAttachments](tag, "litef_resource_attachment")
{
    val resourceId     = column [ String         ] ("resource_id", O.NotNull)
    val format         = column [ String         ] ("format",      O.NotNull)
    val created        = column [ Timestamp      ] ("created",     O.NotNull)
    val modified       = column [ Timestamp      ] ("modified",    O.NotNull)
    val content        = column [ Option[String] ] ("content")

    // Every table needs a * projection with the same type as the table's type parameter
    def * = (
        resourceId ,
        format     ,
        created    ,
        modified   ,
        content
    ) <> (ResourceAttachments.tupled, ResourceAttachments.unapply)
}

object ResourceAttachmentsTable {
    val query = TableQuery[ResourceAttachmentsTable]
}

