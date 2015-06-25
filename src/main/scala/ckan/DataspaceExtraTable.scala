/*
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
//import java.sql.Timestamp
//import spray.json._
//import common.Config

// DataspaceExtra
case class DataspaceExtra(
    id              : String,
    dataspaceId     : Option[String] = None,
    key             : Option[String] = None,
    value           : Option[String] = None,
    state           : Option[String] = None,
    revisionId      : Option[String] = None
)

class DataspaceExtraTable(tag: Tag)
    extends Table[DataspaceExtra](tag, "group_extra")
{
    val id              = column[ String            ] ("id", O.PrimaryKey)
    val dataspaceId     = column[ Option[String]    ] ("group_id")
    val key             = column[ Option[String]    ] ("key")
    val value           = column[ Option[String]    ] ("value")
    val state           = column[ Option[String]    ] ("state")
    val revisionId      = column[ Option[String]    ] ("revision_id")

    // Every table needs a * projection with the same type as the table's type parameter
    def * = (
        id          ,
        dataspaceId ,
        key         ,
        value       ,
        state       ,
        revisionId
    ) <> (DataspaceExtra.tupled, DataspaceExtra.unapply)
    
}

object DataspaceExtraTable {
    val query = TableQuery[DataspaceExtraTable]
}