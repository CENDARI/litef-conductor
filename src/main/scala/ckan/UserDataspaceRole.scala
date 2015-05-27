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
import common.Config.{ Ckan => CkanConfig }

// UserDataspaceRole
case class UserDataspaceRole(
    id            : String,
    userId        : String,
    userApiKey    : String,
    dataspaceRole : String,
    dataspaceId   : String,
    state         : String
)

class UserDataspaceRoleTable(tag: Tag)
    extends Table[UserDataspaceRole](tag, "litef_ckan_user_group_role_view")
{
    val id              = column[ String ] ("id"          , O.NotNull)
    val userId          = column[ String ] ("user_id"     , O.NotNull)
    val userApiKey      = column[ String ] ("user_apikey" , O.NotNull)
    val dataspaceRole   = column[ String ] ("group_role"  , O.NotNull)
    val dataspaceId     = column[ String ] ("group_id"    , O.NotNull)
    val state           = column[ String ] ("state"    , O.NotNull)

    // Every table needs a * projection with the same type as the table's type parameter
    def * = (
        id              ,
        userId          ,
        userApiKey      ,
        dataspaceRole   ,
        dataspaceId     ,
        state
    ) <> (UserDataspaceRole.tupled, UserDataspaceRole.unapply)
}

object UserDataspaceRoleTable {
    val query = TableQuery[UserDataspaceRoleTable]
}

object UserDataspaceRoleJsonProtocol extends DefaultJsonProtocol {
    implicit object UserDataspaceRoleJsonFormat extends RootJsonFormat[UserDataspaceRole] {
        def write(r: UserDataspaceRole) =
            JsObject(
                "id"            -> JsString(r.id),
                "url"           -> JsString(s"${common.Config.namespace}privileges/${r.id}"),
                "userId"        -> JsString(r.userId),
                "userUrl"       -> JsString(s"${common.Config.namespace}users/${r.userId}"),
                "dataspaceId"   -> JsString(r.dataspaceId),
                "dataspaceUrl"  -> JsString(s"${common.Config.namespace}dataspaces/${r.dataspaceId}"),
                "role"          -> JsString(r.dataspaceRole),
                "state"         -> JsString(r.state)
            )

        def read(value: JsValue) = {
            value.asJsObject.getFields("userId", "dataspaceId", "role") match {
                case Seq(JsString(userId), JsString(dataspaceId), JsString(role)) =>
                    role.toLowerCase match {
                        case "admin"|"editor"|"member" => new UserDataspaceRole("", userId, "", role.toLowerCase, dataspaceId, "")
                        case _ => throw new DeserializationException("""Invalid value for role. Valid values are "admin", "editor" and "member" """)
                    }
                case _ => throw new DeserializationException("Invalid JSON input for user dataspace role")
            }
        }
    }

    implicit object UserDataspaceRoleSeqJsonFormat extends RootJsonFormat[List[UserDataspaceRole]] {
        def write(r: List[UserDataspaceRole]) =
            JsArray(r.map{ _.toJson })

        def read(value: JsValue) =
            throw new DeserializationException("List of UserDataspaceRole can not be read from JSON")
    }
}

