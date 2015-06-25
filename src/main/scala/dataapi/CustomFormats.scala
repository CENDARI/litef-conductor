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

import spray.json._
import ckan.DataspaceJsonProtocol._
import java.lang.String
import spray.httpx.unmarshalling.{MalformedContent, FromStringDeserializer}

object StateFilter extends Enumeration {
    type StateFilter = Value
    val ACTIVE, DELETED, ALL = Value
}

object Visibility extends Enumeration {
    type Visibility = Value
    val public, `private` = Value 
}

case class DataspaceCreate (name: String, title: Option[String], description: Option[String], visibility: Option[Visibility.Visibility]){
    require(name matches "[a-z0-9_-]{2,100}")
}
case class DataspaceCreateWithId (id: String, name: String, title: Option[String], description: Option[String], visibility: Option[Visibility.Visibility])
case class DataspaceUpdate (title: Option[String], description: Option[String], visibility: Option[Visibility.Visibility])
case class DataspaceUpdateWithId (id: String, title: Option[String], description: Option[String], visibility: Option[Visibility.Visibility])
//case class Resource(id: String, name: Option[String], description: Option[String], format: Option[String], package_id: String, upload: String )
case class PackageCreateWithId(name: String, owner_org: String, title: String, `private`: Boolean = true) 

case class CkanApiPackageCreate(val name: String, val title: Option[String], val description: Option[String], val dataspaceId: String, val isPrivate: Option[Boolean]){
    require(name matches "[a-z0-9_-]{2,100}")
}

case class CkanApiPackageUpdate(val title: Option[String], val description: Option[String], val isPrivate: Option[Boolean])
case class CkanApiPackageUpdateWithId(val id: String, val title: Option[String], val notes: Option[String], val `private`: Option[Boolean])

case class CkanOrganizationMember(id: String, username: String, role: String) {
    require(role == "admin" || role == "editor" || role == "member")
}

case class CkanApiExtras(id: Option[String] = None, key: String, value: String, state: Option[String] = None, revision_id: Option[String] = None, group_id: Option[String] = None)
case class ShibData(mail: String, eppn: String, cn: String)
case class CkanUser(name: String, email: String, password: String, id: String, fullname: String, openid: String)

case class CkanErrorMsg (message: String, __type: String)
//case class CkanResponse[T](help: String, success: Boolean, result: Option[T], error: Option[CkanErrorMsg])
case class CkanResponse(help: String, success: Boolean, result: Option[Map[String, JsValue]], error: Option[CkanErrorMsg])

object CkanJsonProtocol extends DefaultJsonProtocol {
    implicit object VisibilityJsonFormat extends RootJsonFormat[Visibility.Visibility] {
        def write(v: Visibility.Visibility) = JsString(v.toString)
        
        def read(value: JsValue) = value match {
            case JsString(s) => Visibility.withName(s)
            case _ => throw new DeserializationException(s"Invalid value '$value'. Valid values are 'public' and 'private'")
        }
    }
    implicit val ckanApiExtrasJsonFormat = jsonFormat6(CkanApiExtras)
    implicit val dataspaceCreateFormat = jsonFormat4(DataspaceCreate)
    //implicit val dataspaceCreateWithIdFormat = jsonFormat4(DataspaceCreateWithId)
    implicit object DataspaceCreateWithIdJsonFormat extends RootJsonFormat[DataspaceCreateWithId] {
        def write(d: DataspaceCreateWithId) = 
            JsObject(
                "id"    -> JsString(d.id),
                "name"  -> JsString(d.name),
                "title" -> JsString(d.title.getOrElse("")),
                "description" -> JsString(d.description.getOrElse("")),
                "extras" -> JsArray(
                    d.visibility match {
                        case Some(v) => JsObject("key" -> JsString("visibility"), "value" -> JsString(v.toString))
                        case None => JsObject("key" -> JsString("visibility"), "value" -> JsString("private"))
                    }
                )
            )
        def read(v: JsValue) =
            throw new DeserializationException("DataspaceCreateWithId cannot be read from JSON")
    }
    
    implicit val dataspaceUpdateFormat = jsonFormat3(DataspaceUpdate)
    //implicit val dataspaceUpdateWithIdFormat = jsonFormat4(DataspaceUpdateWithId)
    implicit object DataspaceUpdateWithIdJsonFormat extends RootJsonFormat[DataspaceUpdateWithId] {
        def write(d: DataspaceUpdateWithId) = {
            // TODO: omit field (title/description) if None
            val fields = Map(
                "id"    -> JsString(d.id),
                //"name"  -> JsString(d.name),
                "title" -> d.title.map(JsString(_)).getOrElse(JsNull),
                "description" -> d.description.map(JsString(_)).getOrElse(JsNull)
            )
        
            d.visibility match {
                case Some(v) => JsObject(fields ++ Map("extras" -> JsArray(JsObject("key" -> JsString("visibility"), "value" -> JsString(v.toString)))))
                case None => JsObject(fields)
            }
        }
        def read(v: JsValue) =
            throw new DeserializationException("DataspaceUpdateWithId cannot be read from JSON")
    }
    implicit val packageCreateWithIdFormat = jsonFormat4(PackageCreateWithId)
    implicit val shibDataFormat = jsonFormat3(ShibData)
    implicit val ckanUserFormat = jsonFormat6(CkanUser)
    implicit val ckanErrorMsgFormat = jsonFormat2(CkanErrorMsg)
    //implicit def ckanResponseFormat[T: JsonFormat] = lazyFormat(jsonFormat4(CkanResponse.apply[T]))
    implicit def ckanResponseFormat = jsonFormat4(CkanResponse)
    implicit val ckanOrganizationMember = jsonFormat3(CkanOrganizationMember)
    
    implicit object CkanApiPackageCreateJsonFormat extends RootJsonFormat[CkanApiPackageCreate] {
        def write(p: CkanApiPackageCreate) =
            JsObject(
                "name"          -> JsString(p.name),
                "title"         -> JsString(p.title getOrElse ""),
                "notes"         -> JsString(p.description getOrElse ""),
                "owner_org"     -> JsString(p.dataspaceId),
                "private"       -> JsBoolean(p.isPrivate getOrElse true)
            )

        def read(value: JsValue) = {
            jsonFormat5(CkanApiPackageCreate).read(value)
        }
    }
    
    implicit object CkanApiPackageUpdateJsonFormat extends RootJsonFormat[CkanApiPackageUpdate] {
        def write(p: CkanApiPackageUpdate) = {
            throw new DeserializationException("CkanApiPackageUpdate cannot be transformed to JSON")
        }
        def read(value: JsValue) = 
            jsonFormat3(CkanApiPackageUpdate).read(value)
    }
    
    implicit object CkanApiPackageUpdateWithIdJsonFormat extends RootJsonFormat[CkanApiPackageUpdateWithId] {
        def write(p: CkanApiPackageUpdateWithId) = {
            jsonFormat4(CkanApiPackageUpdateWithId).write(p)
            
        }
        def read(value: JsValue) = 
            throw new DeserializationException("CkanApiPackageUpdateWithId cannot be read from JSON")
    }
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