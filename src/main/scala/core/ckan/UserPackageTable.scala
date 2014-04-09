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

case class UserPackage(
    user_id          : String,
    user_apikey      : String,
    package_role     : String,
    id               : String,
    name             : String,
    title            : String,
    version          : String,
    url              : String,
    notes            : String,
    license_id       : String,
    revision_id      : String,
    author           : String,
    author_email     : String,
    maintainer       : String,
    maintainer_email : String,
    state            : String,
    `type`           : String,
    owner_org        : String,
    `private`        : String
)

// Was:
// SELECT package_r.user_id, package_r.user_apikey, package_r.package_role, package.id, package.name, package.title, package.version, package.url, package.notes, package.license_id, package.revision_id, package.author, package.author_email, package.maintainer, package.maintainer_email, package.state, package.type, package.owner_org, package.private
//    FROM litef_ckan_user_package_role_view package_r
//    JOIN package ON package_r.package_id = package.id
// UNION
//    SELECT group_r.user_id, group_r.user_apikey, group_r.group_role AS package_role, package.id, package.name, package.title, package.version, package.url, package.notes, package.license_id, package.revision_id, package.author, package.author_email, package.maintainer, package.maintainer_email, package.state, package.type, package.owner_org, package.private
//    FROM litef_ckan_user_group_role_view group_r
//    JOIN "group" ON group_r.group_id = "group".id
//    JOIN package ON package.owner_org = "group".id;
// Now, just the second part of the union

class UserPackageTable(tag: Tag)
    extends Table[core.ckan.UserPackage](tag, "litef_ckan_user_package_view")
{
    val id            = column[ String            ]  ("id", O.PrimaryKey)
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
    ) <> (UserPackage.tupled, UserPackage.unapply)
}

object UserPackageTable {
    val query = TableQuery[UserPackageTable]
}

object UserResourceJsonProtocol extends DefaultJsonProtocol {
    implicit object UserResourceJsonFormat extends RootJsonFormat[UserPackage] {
        def write(rs: UserPackage) =
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
            throw new DeserializationException("UserResource can not be read from JSON")
        }
    }

    implicit object UserResourceSeqJsonFormat extends RootJsonFormat[List[UserPackage]] {
        def write(ds: List[UserPackage]) =
            JsArray(ds.map{ _.toJson })

        def read(value: JsValue) =
            throw new DeserializationException("UserResource can not be read from JSON")
    }
}

