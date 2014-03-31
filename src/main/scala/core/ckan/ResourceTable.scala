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

case class Resource(
    id            : String,
    resourceGroup : Option[String]    = None,
    url           : String,
    format        : Option[String]    = None,
    description   : Option[String]    = None,
    position      : Option[Int]       = None,
    revision_id   : Option[String]    = None,
    hash          : Option[String]    = None,
    state         : Option[String]    = None,
    extras        : Option[String]    = None,
    name          : Option[String]    = None,
    resource_type : Option[String]    = None,
    mimetype      : Option[String]    = None,
    mimetypeInner : Option[String]    = None,
    size          : Option[Long]      = None,
    modified      : Option[Timestamp] = None,
    created       : Option[Timestamp] = None,
    cacheUrl      : Option[String]    = None
)

class ResourceTable(tag: Tag)
    extends Table[core.ckan.Resource](tag, "litef_ckan_resource_view")
{
    def id            = column[ String            ]  ("id", O.PrimaryKey)
    def url           = column[ String            ]  ("url", O.NotNull)
    def resourceGroup = column[ Option[String]    ]  ("resource_group_id")
    def format        = column[ Option[String]    ]  ("format")
    def description   = column[ Option[String]    ]  ("description")
    def position      = column[ Option[Int]       ]  ("position")
    def revisionId    = column[ Option[String]    ]  ("revision_id")
    def hash          = column[ Option[String]    ]  ("hash")
    def state         = column[ Option[String]    ]  ("state")
    def extras        = column[ Option[String]    ]  ("extras")
    def name          = column[ Option[String]    ]  ("name")
    def resourceType  = column[ Option[String]    ]  ("resource_type")
    def mimetype      = column[ Option[String]    ]  ("mimetype")
    def mimetypeInner = column[ Option[String]    ]  ("mimetype_inner")
    def size          = column[ Option[Long]      ]  ("size")
    def modified      = column[ Option[Timestamp] ]  ("modified") // greatest(last_modified, created), better than just coalesce
    def created       = column[ Option[Timestamp] ]  ("created")
    def cacheUrl      = column[ Option[String]    ]  ("cache_url")

    // Every table needs a * projection with the same type as the table's type parameter
    def * = (
        id             ,
        resourceGroup  ,
        url            ,
        format         ,
        description    ,
        position       ,
        revisionId     ,
        hash           ,
        state          ,
        extras         ,
        name           ,
        resourceType   ,
        mimetype       ,
        mimetypeInner  ,
        size           ,
        modified       ,
        created        ,
        cacheUrl
    ) <> (Resource.tupled, Resource.unapply)
}

object ResourceTable {
    val query = TableQuery[ResourceTable]
}

object ResourceJsonProtocol extends DefaultJsonProtocol {
    implicit object ResourceJsonFormat extends RootJsonFormat[Resource] {
        def write(rs: Resource) =
            JsObject(
                "id"             -> JsString(rs.id),

                "dataUrl"        -> JsString(rs.url),
                "cacheUrl"       -> JsString(rs.cacheUrl getOrElse ""),

                "name"           -> JsString(rs.name     getOrElse ""),
                "format"         -> JsString(rs.format   getOrElse ""),
                "mimetype"       -> JsString(rs.mimetype getOrElse ""),
                "size"           -> JsNumber(rs.size     getOrElse 0L),

                "created"        -> JsNumber(rs.created.map  { _.getTime } getOrElse 0L),
                "modified"       -> JsNumber(rs.modified.map { _.getTime } getOrElse 0L)
            )

        def read(value: JsValue) = {
            throw new DeserializationException("Resource can not be read from JSON")
        }
    }

    implicit object ResourceSeqJsonFormat extends RootJsonFormat[List[Resource]] {
        def write(ds: List[Resource]) =
            JsArray(ds.map{ _.toJson })

        def read(value: JsValue) =
            throw new DeserializationException("Resource can not be read from JSON")
    }
}

