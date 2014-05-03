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

// ProcessedResource
case class ProcessedResource(
    id             : String,
    lastProcessed  : Option[Timestamp]
)

class ProcessedResourceTable(tag: Tag)
    extends Table[ProcessedResource](tag, "litef_processed_resource")
{
    val id             = column [ String            ] ("resource_id",    O.NotNull, O.PrimaryKey)
    val lastProcessed  = column [ Option[Timestamp] ] ("last_processed", O.Nullable, O.Default(None))

    // We can not make FK on a view :/
    // val resourceFKey   = foreignKey("litef_scheduled_resource_resource_fk",
    //                                 resourceId, ckan.ResourceTable.query)(_.id, onDelete = ForeignKeyAction.Cascade)

    // Every table needs a * projection with the same type as the table's type parameter
    def * = (
        id ,
        lastProcessed
    ) <> (ProcessedResource.tupled, ProcessedResource.unapply)
}

object ProcessedResourceTable {
    val query = TableQuery[ProcessedResourceTable]
}

