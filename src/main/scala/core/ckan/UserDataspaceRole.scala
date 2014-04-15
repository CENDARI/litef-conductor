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
import spray.json._

// UserDataspaceRole
case class UserDataspaceRole(
    userId        : String,
    userApiKey    : String,
    dataspaceRole : String,
    dataspaceId   : String
)

class UserDataspaceRoleTable(tag: Tag)
    extends Table[UserDataspaceRole](tag, "litef_ckan_user_group_role_view")
{
    val userId        = column[ String ] ("user_id"     , O.NotNull)
    val userApiKey    = column[ String ] ("user_apikey" , O.NotNull)
    val dataspaceRole = column[ String ] ("group_role"  , O.NotNull)
    val dataspaceId   = column[ String ] ("group_id"    , O.NotNull)

    // Every table needs a * projection with the same type as the table's type parameter
    def * = (
        userId        ,
        userApiKey    ,
        dataspaceRole ,
        dataspaceId
    ) <> (UserDataspaceRole.tupled, UserDataspaceRole.unapply)
}

object UserDataspaceRoleTable {
    val query = TableQuery[UserDataspaceRoleTable]
}

