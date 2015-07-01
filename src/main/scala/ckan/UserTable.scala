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
import spray.json._
import java.security.MessageDigest

// User
case class User(
    id          : String,
    username    : String,
    fullname    : Option[String]    = None,
    apikey      : Option[String]    = None,
    email       : Option[String]    = None,
    openid      : Option[String]    = None,
    state       : String,
    about       : Option[String]    = None,
    sysadmin    : Option[Boolean]   = Some(false)
){
    lazy val emailHash = email match {
        case None | Some("")    => ""
        case Some(e)            => (MessageDigest.getInstance("MD5"))
                                    .digest(e.trim().toLowerCase().getBytes("UTF-8"))
                                    .map("%02x".format(_)).mkString
    }
}

class UserTable(tag: Tag)
    extends Table[User](tag, "user")
{
    val id       = column[ String         ] ("id", O.PrimaryKey)
    val username = column[ String         ] ("name", O.NotNull)
    val fullname = column[ Option[String] ] ("fullname")
    val apikey   = column[ Option[String] ] ("apikey")
    val email    = column[ Option[String] ] ("email")
    val openid   = column[ Option[String] ] ("openid")
    val state    = column[ String         ] ("state", O.NotNull)
    val about    = column[ Option[String] ] ("about")
    val sysadmin = column[ Option[Boolean]] ("sysadmin", O.Default(Some(false)))

    // Every table needs a * projection with the same type as the table's type parameter
    def * = (
        id          ,
        username    ,
        fullname    ,
        apikey      ,
        email       ,
        openid      ,
        state       ,
        about       ,
        sysadmin
    ) <> (User.tupled, User.unapply)

}

object UserTable {
    val query = TableQuery[UserTable]
}

object UserJsonProtocol extends DefaultJsonProtocol {
    implicit object UserJsonFormat extends RootJsonFormat[User] {
        def write(u: User) =
            JsObject(
                "id"            -> JsString(u.id),
                "url"           -> JsString(s"${common.Config.namespace}users/${u.id}"),
                "username"      -> JsString(u.username),
                "fullname"      -> JsString(u.fullname getOrElse ""),
                "about"         -> JsString(u.about getOrElse ""),
                "emailHash"     -> JsString(u.emailHash),
                "sysadmin"      -> JsBoolean(u.sysadmin getOrElse false),
                "state"         -> JsString(u.state)
            )

        def read(value: JsValue) = {
            throw new DeserializationException("User can not be read from JSON")
        }
    }

    implicit object UserSeqJsonFormat extends RootJsonFormat[List[User]] {
        def write(u: List[User]) =
            JsArray(u.map{ _.toJson })

        def read(value: JsValue) =
            throw new DeserializationException("User can not be read from JSON")
    }
}
