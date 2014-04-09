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

// Dataspace
case class Dataspace(
    id             : String,
    name           : String,
    dsType         : String,
    isOrganization : Boolean           = false,
    title          : Option[String]    = None,
    description    : Option[String]    = None,
    created        : Option[Timestamp] = None,
    state          : Option[String]    = None,
    revisionId     : Option[String]    = None,
    approvalStatus : Option[String]    = None,
    imageUrl       : Option[String]    = None
)

class DataspaceTable(tag: Tag)
    extends Table[Dataspace](tag, "group")
{
    val id             = column[ String            ] ("id", O.PrimaryKey)
    val name           = column[ String            ] ("name", O.NotNull)
    val dsType         = column[ String            ] ("type", O.NotNull)
    val isOrganization = column[ Boolean           ] ("is_organization", O.NotNull)
    val title          = column[ Option[String]    ] ("title")
    val description    = column[ Option[String]    ] ("description")
    val created        = column[ Option[Timestamp] ] ("created")
    val state          = column[ Option[String]    ] ("state")
    val revisionId     = column[ Option[String]    ] ("revision_id")
    val approvalStatus = column[ Option[String]    ] ("approval_status")
    val imageUrl       = column[ Option[String]    ] ("image_url")

    // Every table needs a * projection with the same type as the table's type parameter
    def * = (
        id             ,
        name           ,
        dsType         ,
        isOrganization ,
        title          ,
        description    ,
        created        ,
        state          ,
        revisionId     ,
        approvalStatus ,
        imageUrl
    ) <> (Dataspace.tupled, Dataspace.unapply)
}

object DataspaceTable {
    val query = TableQuery[DataspaceTable]
}

object DataspaceJsonProtocol extends DefaultJsonProtocol {
    implicit object DataspaceJsonFormat extends RootJsonFormat[Dataspace] {
        def write(ds: Dataspace) =
            JsObject(
                "id"             -> JsString(ds.id),

                "name"           -> JsString(ds.name),
                "title"          -> JsString(ds.title       getOrElse ""),
                "description"    -> JsString(ds.description getOrElse ""),
                // "image"          -> JsString(ds.imageUrl getOrElse ""),

                "isOrganization" -> JsBoolean(ds.isOrganization),
                "type"           -> JsString(ds.dsType),
                "state"          -> JsString(ds.state getOrElse "")
            )

        def read(value: JsValue) = {
            throw new DeserializationException("Dataspace can not be read from JSON")
        }
    }

    implicit object DataspaceSeqJsonFormat extends RootJsonFormat[List[Dataspace]] {
        def write(ds: List[Dataspace]) =
            JsArray(ds.map{ _.toJson })

        def read(value: JsValue) =
            throw new DeserializationException("Dataspace can not be read from JSON")
    }
}

