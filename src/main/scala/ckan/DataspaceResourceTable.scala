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
import common.Config

case class DataspaceResource(
    dataspaceId   : String,
    resourceId    : String,
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
    cacheUrl      : Option[String]    = None,
    packageId     : Option[String]    = None
)

case class DataspaceResourcePair(
    dataspaceId   : String,
    resourceId    : String
)

class DataspaceResourceTable(tag: Tag)
    extends Table[DataspaceResource](tag, "litef_ckan_group_resource_view")
{
    val dataspaceId   = column[ String            ]  ("group_id", O.NotNull)
    val resourceId    = column[ String            ]  ("id", O.NotNull)
    val url           = column[ String            ]  ("url", O.NotNull)
    val resourceGroup = column[ Option[String]    ]  ("resource_group_id")
    val format        = column[ Option[String]    ]  ("format")
    val description   = column[ Option[String]    ]  ("description")
    val position      = column[ Option[Int]       ]  ("position")
    val revisionId    = column[ Option[String]    ]  ("revision_id")
    val hash          = column[ Option[String]    ]  ("hash")
    val state         = column[ Option[String]    ]  ("state")
    val extras        = column[ Option[String]    ]  ("extras")
    val name          = column[ Option[String]    ]  ("name")
    val resourceType  = column[ Option[String]    ]  ("resource_type")
    val mimetype      = column[ Option[String]    ]  ("mimetype")
    val mimetypeInner = column[ Option[String]    ]  ("mimetype_inner")
    val size          = column[ Option[Long]      ]  ("size")
    val modified      = column[ Option[Timestamp] ]  ("modified") // greatest(last_modified, created), better than just coalesce
    val created       = column[ Option[Timestamp] ]  ("created")
    val cacheUrl      = column[ Option[String]    ]  ("cache_url")
    val packageId     = column[ Option[String]    ]  ("package_id")

    // Every table needs a * projection with the same type as the table's type parameter
    def * = (
        dataspaceId    ,
        resourceId     ,
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
        cacheUrl       ,
        packageId
    ) <> (DataspaceResource.tupled, DataspaceResource.unapply)

    def justIds = (
        dataspaceId,
        resourceId
    )
}

object DataspaceResourceTable {
    val query = TableQuery[DataspaceResourceTable]
}

// TODO: This needs to be united with ResourceJsonProtocol
object DataspaceResourceJsonProtocol extends DefaultJsonProtocol {
    implicit object DataspaceResourceJsonFormat extends RootJsonFormat[DataspaceResource] {
        def write(rs: DataspaceResource) =
            JsObject(
                "id"             -> JsString(rs.resourceId),

                "url"        -> JsString(s"${Config.namespace}resources/${rs.resourceId}"),
                "dataUrl"        -> JsString(s"${Config.namespace}resources/${rs.resourceId}/data"),

                "name"           -> JsString(rs.name     getOrElse ""),
                "format"         -> JsString(rs.format   getOrElse ""),
                "mimetype"       -> JsString(rs.mimetype getOrElse ""),
                "size"           -> JsNumber(rs.size     getOrElse 0L),

                "created"        -> JsNumber(rs.created map  { _.getTime } getOrElse 0L),
                "modified"       -> JsNumber(rs.modified map { _.getTime } getOrElse 0L),
                "set_id"         -> JsString(rs.packageId getOrElse "")
            )

        def read(value: JsValue) = {
            throw new DeserializationException("DataspaceResource can not be read from JSON")
        }
    }

    implicit object DataspaceResourceSeqJsonFormat extends RootJsonFormat[List[DataspaceResource]] {
        def write(ds: List[DataspaceResource]) =
            JsArray(ds.map{ _.toJson }.toVector)

        def read(value: JsValue) =
            throw new DeserializationException("DataspaceResource can not be read from JSON")
    }
}

