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
package core.ckan

import slick.driver.PostgresDriver.simple._
import java.sql.Timestamp

case class Dataspace(
    id             : String,
    name           : String,
    title          : Option[String]    = None,
    description    : Option[String]    = None,
    created        : Option[Timestamp] = None,
    state          : Option[String]    = None,
    revisionId     : Option[String]    = None,
    dsType         : String,
    approvalStatus : Option[String]    = None,
    imageUrl       : Option[String]    = None,
    isOrganization : Boolean           = false
)

class DataspaceTable(tag: Tag)
    extends Table[Dataspace](tag, "group")
{
    def id             = column[String]("id", O.PrimaryKey)
    def name           = column[String]("name", O.NotNull)
    def title          = column[Option[String]]("title")
    def description    = column[Option[String]]("description")
    def created        = column[Option[Timestamp]]("created")
    def state          = column[Option[String]]("state")
    def revisionId     = column[Option[String]]("revision_id")
    def dsType         = column[String]("type", O.NotNull)
    def approvalStatus = column[Option[String]]("approval_status")
    def imageUrl       = column[Option[String]]("image_url")
    def isOrganization = column[Boolean]("is_organization", O.NotNull)

    // Every table needs a * projection with the same type as the table's type parameter
    def * = (
        id             ,
        name           ,
        title          ,
        description    ,
        created        ,
        state          ,
        revisionId     ,
        dsType         ,
        approvalStatus ,
        imageUrl       ,
        isOrganization
    ) <> (Dataspace.tupled, Dataspace.unapply)
}

object DataspaceTable {
    val query = TableQuery[DataspaceTable]
}
