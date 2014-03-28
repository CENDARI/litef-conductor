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
import scala.slick.lifted
import common.Config.Ckan.{Database => CkanDatabaseConfig}

object CkanInterface {
    lazy val database = Database.forURL(
        CkanDatabaseConfig.url,
        user     = CkanDatabaseConfig.user,
        password = CkanDatabaseConfig.password,
        driver   = CkanDatabaseConfig.driver
    )
    
    def getResourcesQuery(since: Option[Timestamp], until: Option[Timestamp]) = database withSession { implicit session: Session =>

        var result: lifted.Query[ResourceTable, ResourceTable#TableElementType] =
            ResourceTable.query

        if (since.isDefined)
            result = result.withFilter(_.modified >= since.get)

        if (until.isDefined)
            result = result.withFilter(_.modified <= until.get)

        result.sortBy(_.modified asc)
    }

    def getResourceQuery(id: String) = database withSession { implicit session: Session =>
        ResourceTable.query.withFilter(_.id === id)
    }

    def getResource(id: String): Option[Resource] = database withSession { implicit session: Session =>
        val results = getResourceQuery(id).list

        if (results.size > 0)
            Some(results(0))
        else
            None
    }

    def getDataspacesQuery(since: Option[Timestamp], until: Option[Timestamp]) = database withSession { implicit session: Session =>

        var result: lifted.Query[DataspaceTable, DataspaceTable#TableElementType] =
            DataspaceTable.query

        if (since.isDefined)
            result = result.withFilter(_.created >= since.get)

        if (until.isDefined)
            result = result.withFilter(_.created <= until.get)

        result.sortBy(_.created asc)
    }
}
