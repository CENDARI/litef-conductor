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

case class UserPackage(
    userId          : String,
    userApiKey      : String,
    packageRole     : Option[String],
    id              : String,
    name            : String,
    title           : Option[String],
    version         : Option[String],
    url             : Option[String],
    notes           : Option[String],
    licenseId       : Option[String],
    revisionId      : Option[String],
    author          : Option[String],
    authorEmail     : Option[String],
    maintainer      : Option[String],
    maintainerEmail : Option[String],
    state           : Option[String],
    `type`          : Option[String],
    ownerOrg        : Option[String],
    `private`       : Boolean
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
    extends Table[ckan.UserPackage](tag, "litef_ckan_user_package_view")
{
    val userId          = column[ String          ] ("user_id", O.NotNull)
    val userApiKey      = column[ String          ] ("user_apikey", O.NotNull)
    val packageRole     = column[ Option[String]  ] ("package_role")
    val id              = column[ String          ] ("id", O.NotNull)
    val name            = column[ String          ] ("name", O.NotNull)
    val title           = column[ Option[String]  ] ("title")
    val version         = column[ Option[String]  ] ("version")
    val url             = column[ Option[String]  ] ("url")
    val notes           = column[ Option[String]  ] ("notes")
    val licenseId       = column[ Option[String]  ] ("license_id")
    val revisionId      = column[ Option[String]  ] ("revision_id")
    val author          = column[ Option[String]  ] ("author")
    val authorEmail     = column[ Option[String]  ] ("author_email")
    val maintainer      = column[ Option[String]  ] ("maintainer")
    val maintainerEmail = column[ Option[String]  ] ("maintainer_email")
    val state           = column[ Option[String]  ] ("state")
    val `type`          = column[ Option[String]  ] ("type")
    val ownerOrg        = column[ Option[String]  ] ("owner_or")
    val `private`       = column[ Boolean         ] ("title")

    // Every table needs a * projection with the same type as the table's type parameter
    def * = (
        userId          ,
        userApiKey      ,
        packageRole     ,
        id              ,
        name            ,
        title           ,
        version         ,
        url             ,
        notes           ,
        licenseId       ,
        revisionId      ,
        author          ,
        authorEmail     ,
        maintainer      ,
        maintainerEmail ,
        state           ,
        `type`          ,
        ownerOrg        ,
        `private`
    ) <> (UserPackage.tupled, UserPackage.unapply)
}

object UserPackageTable {
    val query = TableQuery[UserPackageTable]
}

object UserResourceJsonProtocol extends DefaultJsonProtocol {
    implicit object UserResourceJsonFormat extends RootJsonFormat[UserPackage] {
        def write(up: UserPackage) =
            JsObject(
                "id"             -> JsString(up.id),
                "url"            -> JsString(up.url   getOrElse ""),
                "name"           -> JsString(up.name),
                "title"          -> JsString(up.title getOrElse "")
            )

        def read(value: JsValue) = {
            throw new DeserializationException("UserResource can not be read from JSON")
        }
    }

    implicit object UserResourceSeqJsonFormat extends RootJsonFormat[List[UserPackage]] {
        def write(ds: List[UserPackage]) =
            JsArray(ds.map{ _.toJson }.toVector)

        def read(value: JsValue) =
            throw new DeserializationException("UserResource can not be read from JSON")
    }
}

