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
import scala.slick.model.ForeignKeyAction
import java.sql.Timestamp
import spray.json._
import common.Config

// ResourceAttachment
case class ResourceAttachment(
    resourceId     : String,
    format         : String,
    created        : Timestamp = null,
    modified       : Timestamp = null,
    content        : Option[String] = None
)

object ResourceAttachmentUtil {
    implicit class ResourceAttachmentImplicits(attachment: ResourceAttachment) {
        def localDirectory: String = {
            val choppedId =
                (attachment.resourceId take 3) + '/' +
                (attachment.resourceId drop 3 take 3) + '/' +
                (attachment.resourceId drop 6)

            val result = Config.Indexer.localStoragePrefix + '/' + choppedId

            val dir = new java.io.File(result)

            if (!dir.exists && !dir.mkdirs) {
                throw new RuntimeException(s"Can not create indexer data directory $dir")
            }

            result
        }

        def fileName: String =
            attachment.format.replace('/', ':')

        def localPath: String =
            localDirectory + '/' + fileName
    }
}

class ResourceAttachmentTable(tag: Tag)
    extends Table[ResourceAttachment](tag, "litef_resource_attachment")
{
    val resourceId     = column [ String         ] ("resource_id", O.NotNull)
    val format         = column [ String         ] ("format",      O.NotNull)
    val created        = column [ Timestamp      ] ("created",     O.NotNull)
    val modified       = column [ Timestamp      ] ("modified",    O.NotNull)
    val content        = column [ Option[String] ] ("content",     O.DBType("text"))

    val pk             = primaryKey("litef_resource_attachment_pk", (resourceId, format))

    // We can not make FK on a view :/
    // val resourceFKey   = foreignKey("litef_resource_attachment_resource_fk",
    //                                 resourceId, ckan.ResourceTable.query)(_.id, onDelete = ForeignKeyAction.Cascade)


    // Every table needs a * projection with the same type as the table's type parameter
    def * = (
        resourceId ,
        format     ,
        created    ,
        modified   ,
        content
    ) <> (ResourceAttachment.tupled, ResourceAttachment.unapply)
}

object ResourceAttachmentTable {
    val query = TableQuery[ResourceAttachmentTable]
}

