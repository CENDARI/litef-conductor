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
import spray.json.JsonFormat
import spray.httpx.unmarshalling.{MalformedContent, FromStringDeserializer}

case class DataspaceCreate (name: String, title: Option[String], description: Option[String]){
    require(name matches "[a-z0-9_-]+")
}
case class DataspaceCreateWithId (id: String, name: String, title: Option[String], description: Option[String])
case class DataspaceUpdate (title: Option[String], description: Option[String])
case class DataspaceUpdateWithId (id: String, title: Option[String], description: Option[String])
//case class Resource(id: String, name: Option[String], description: Option[String], format: Option[String], package_id: String, upload: String )
case class PackageCreateWithId(name: String, owner_org: String, title: String, `private`: Boolean = true)
case class CkanOrganizationMember(id: String, username: String, role: String) {
    require(role == "admin" || role == "editor" || role == "member")
}

case class ShibData(mail: String, eppn: String, cn: String)
case class CkanUser(name: String, email: String, password: String, id: String, fullname: String, openid: String)

case class CkanErrorMsg (message: String, __type: String)
case class CkanResponse[T](help: String, success: Boolean, result: Option[T], error: Option[CkanErrorMsg])

object StateFilter extends Enumeration {
    type StateFilter = Value
    val ACTIVE, DELETED, ALL = Value
}

//object VisibilityFilter extends Enumeration {
//    type VisibilityFilter = Value
//    val PRIVATE, PUBLIC, ALL = Value
//}

object CkanJsonProtocol extends DefaultJsonProtocol {
    implicit val dataspaceCreateFormat = jsonFormat3(DataspaceCreate)
    implicit val dataspaceCreateWithIdFormat = jsonFormat4(DataspaceCreateWithId)
    implicit val dataspaceUpdateFormat = jsonFormat2(DataspaceUpdate)
    implicit val dataspaceUpdateWithIdFormat = jsonFormat3(DataspaceUpdateWithId)
    implicit val packageCreateWithIdFormat = jsonFormat4(PackageCreateWithId)
    implicit val shibDataFormat = jsonFormat3(ShibData)
    implicit val ckanUserFormat = jsonFormat6(CkanUser)
    implicit val ckanErrorMsgFormat = jsonFormat2(CkanErrorMsg)
    implicit def ckanResponseFormat[T: JsonFormat] = lazyFormat(jsonFormat4(CkanResponse.apply[T]))
    implicit val ckanOrganizationMember = jsonFormat3(CkanOrganizationMember)
}

object StateFilterProtocol {
    implicit val stringToStateFilter = new FromStringDeserializer[StateFilter.StateFilter] {
        def apply(value: String) = {
            val name = value.toUpperCase
            if (name == StateFilter.ACTIVE.toString) { Right(StateFilter.ACTIVE) }
            else if (name == StateFilter.DELETED.toString) { Right(StateFilter.DELETED) }
            else if (name == StateFilter.ALL.toString) { Right(StateFilter.ALL) }
            else Left(MalformedContent(s"Invalid value '$value'. Valid values are 'active', 'deleted', and 'all'"))
        }
    }
}