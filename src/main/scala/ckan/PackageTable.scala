/*
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
 * along with this program. If not, see <http://www.gnu.org/licenses/>
 */

package ckan

import slick.driver.PostgresDriver.simple._
import java.sql.Timestamp
import spray.json._
import common.Config

case class Package(
    id          : String,
    name        : String,
    title       : Option[String]    = None,
    description : Option[String]    = None,
    state       : Option[String]    = None,
    dataspaceId : Option[String]    = None,
    `private`   : Option[Boolean]   = Some(false)
)

class PackageTable(tag: Tag) extends Table[Package](tag, "package") {
    val id          = column[String]("id", O.PrimaryKey)
    val name        = column[String]("name")
    val title       = column[Option[String]]("title")
    val description = column[Option[String]]("notes")
    val state       = column[Option[String]]("state")
    val dataspaceId = column[Option[String]]("owner_org")
    val `private`   = column[Option[Boolean]]("private", O.Default(Some(false)))
    
    def * = (id, name, title, description, state, dataspaceId, `private`) <> (Package.tupled, Package.unapply)
    
    def justIds = (dataspaceId,id)
}

object PackageTable { val query = TableQuery[PackageTable] }

object PackageJsonProtocol extends DefaultJsonProtocol {
    implicit object PackageJsonFormat extends RootJsonFormat[Package] {
        def write(p: Package) =
            JsObject(
                "id"            -> JsString(p.id),
                "url"           -> JsString(s"${Config.namespace}sets/${p.id}"),
                "resources"     -> JsString(s"${Config.namespace}sets/${p.id}/resources"),
                "name"          -> JsString(p.name),
                "title"         -> JsString(p.title getOrElse ""),
                "description"   -> JsString(p.description getOrElse ""),
                "dataspaceId"   -> JsString(p.dataspaceId getOrElse ""),
                "dataspaceUrl"  -> JsString(p.dataspaceId map{ s"${Config.namespace}dataspaces/" + _ } getOrElse ""),
                "private"       -> JsBoolean(p.`private` getOrElse false),
                "state"         -> JsString(p.state getOrElse "")
            )

        def read(value: JsValue) = {
            throw new DeserializationException("Package can not be read from JSON")
        }
    }

    implicit object PackageSeqJsonFormat extends RootJsonFormat[List[Package]] {
        def write(p: List[Package]) =
            JsArray(p.map{ _.toJson })

        def read(value: JsValue) =
            throw new DeserializationException("Package can not be read from JSON")
    }
}