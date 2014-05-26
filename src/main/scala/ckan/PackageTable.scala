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

case class Package(
    id: String,
    name: String,
    title: Option[String],
    state: Option[String],
    `type`: Option[String],
    ownerOrg: Option[String],
    `private`: Option[Boolean]=Some(false))

class PackageTable(tag: Tag) extends Table[Package](tag, "package") {
    def * = (id, name, title, state, `type`, ownerOrg, `private`) <> (Package.tupled, Package.unapply)

    val id: Column[String] = column[String]("id", O.PrimaryKey)
    val name: Column[String] = column[String]("name")
    val title: Column[Option[String]] = column[Option[String]]("title")
    val state: Column[Option[String]] = column[Option[String]]("state")
    val `type`: Column[Option[String]] = column[Option[String]]("type")
    val ownerOrg: Column[Option[String]] = column[Option[String]]("owner_org")
    val `private`: Column[Option[Boolean]] = column[Option[Boolean]]("private")
}

object PackageTable { val query = TableQuery[PackageTable] }