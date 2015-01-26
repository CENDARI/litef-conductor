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
    lastProcessed  : Option[Timestamp],
    attachment     : Option[String]
)

class ProcessedResourceTable(tag: Tag)
    extends Table[ProcessedResource](tag, "litef_processed_resource")
{
    val id             = column [ String            ] ("resource_id",    O.NotNull, O.PrimaryKey)
    val lastProcessed  = column [ Option[Timestamp] ] ("last_processed", O.Nullable, O.Default(None))
    val attachment     = column [ String            ] ("attachment",     O.NotNull, O.Default(""), O.PrimaryKey)

    // We can not make FK on a view :/
    // val resourceFKey   = foreignKey("litef_scheduled_resource_resource_fk",
    //                                 resourceId, ckan.ResourceTable.query)(_.id, onDelete = ForeignKeyAction.Cascade)

    // Every table needs a * projection with the same type as the table's type parameter
    def * = (
        id ,
        lastProcessed,
        attachment
    ) <> (to _, from _)

    private def to(pr: (String, Option[Timestamp], String)) =
        ProcessedResource(pr._1, pr._2, if (pr._3.isEmpty) None else Some(pr._3))

    private def from(pr: ProcessedResource) =
        Some((pr.id, pr.lastProcessed, pr.attachment getOrElse ""))
}

object ProcessedResourceTable {
    val query = TableQuery[ProcessedResourceTable]
}

