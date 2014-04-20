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
package ckan

import slick.driver.PostgresDriver.simple._
import java.sql.Timestamp
import spray.json._

// ResourceGroup
case class ResourceGroup(
    resourceId  : String,
    packageId   : String,
    label       : Option[String] = None,
    sort_order  : Option[String] = None,
    extras      : Option[String] = None,
    state       : Option[String] = None,
    revisionId  : Option[String] = None
)

class ResourceGroupTable(tag: Tag)
    extends Table[ResourceGroup](tag, "resource_group")
{
    val resourceId  = column[ String         ] ("id", O.NotNull)
    val packageId   = column[ String         ] ("package_id", O.NotNull)
    val label       = column[ Option[String] ] ("label")
    val sortOrder   = column[ Option[String] ] ("sort_order")
    val extras      = column[ Option[String] ] ("extras")
    val state       = column[ Option[String] ] ("state")
    val revisionId  = column[ Option[String] ] ("revisionId")

    // Every table needs a * projection with the same type as the table's type parameter
    def * = (
        resourceId  ,
        packageId   ,
        label       ,
        sortOrder   ,
        extras      ,
        state       ,
        revisionId
    ) <> (ResourceGroup.tupled, ResourceGroup.unapply)
}

object ResourceGroupTable {
    val query = TableQuery[ResourceGroupTable]
}

