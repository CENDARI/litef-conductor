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
package dataapi

import spray.json.DefaultJsonProtocol
import ckan.DataspaceJsonProtocol._
import java.lang.String

case class DataspaceCreate (name: String, title: Option[String], description: Option[String]){
    require(name matches "[a-z0-9_-]+")
}
case class DataspaceCreateWithId (id: String, name: String, title: Option[String], description: Option[String])
case class DataspaceUpdate (name: Option[String], title: Option[String], description: Option[String]){
    require(name.getOrElse("") matches "[a-z0-9_-]*")
}
case class DataspaceUpdateWithId (id: String, name: Option[String], title: Option[String], description: Option[String])
case class ResourceMetadataCreateWithId (id: String, name: Option[String], description: Option[String], format: Option[String], package_id: String, url: String )
case class ResourceMetadataUpdateWithId(id: String, name: Option[String], description: Option[String], format: Option[String], url: String)
case class PackageCreateWithId(name: String, owner_org: String, title: String = "API Dataset", `private`: Boolean = true)

object CkanJsonProtocol extends DefaultJsonProtocol {
    implicit val dataspaceCreateFormat = jsonFormat3(DataspaceCreate)
    implicit val dataspaceCreateWithIdFormat = jsonFormat4(DataspaceCreateWithId)
    implicit val dataspaceUpdateFormat = jsonFormat3(DataspaceUpdate)
    implicit val dataspaceUpdateWithIdFormat = jsonFormat4(DataspaceUpdateWithId)
    implicit val resourceMetadataCreateWithIdFormat = jsonFormat6(ResourceMetadataCreateWithId)
    implicit val resourceMetadataUpdateWithIdFormat = jsonFormat5(ResourceMetadataUpdateWithId)
    implicit val packageCreateWithIdFormat = jsonFormat4(PackageCreateWithId)
}