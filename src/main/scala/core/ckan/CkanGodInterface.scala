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
import scala.slick.model.Table
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.BASE64EncoderStream
import sun.misc.{BASE64Decoder, BASE64Encoder}
import java.nio.ByteBuffer
import scala.util.Try

object CkanGodInterface {
    val queryResultDefaultLimit = 10
    val queryResultMaximumLimit = 20

    lazy val database = Database.forURL(
        CkanDatabaseConfig.url,
        user     = CkanDatabaseConfig.user,
        password = CkanDatabaseConfig.password,
        driver   = CkanDatabaseConfig.driver
    )

    def getResourceQuery(id: String) = database withSession { implicit session: Session =>
        ResourceTable.query.withFilter(_.id === id)
    }

    def getResource(id: String): Option[Resource] = {
        database withSession { implicit session: Session =>
            getResourceQuery(id).list.headOption
        }
    }

    def listResourcesQuery(_since: Option[Timestamp], _until: Option[Timestamp],
                           start: Int, _count: Int) = database withSession { implicit session: Session =>

        val since = _since.getOrElse(new Timestamp(0))
        val until = _until.getOrElse(new Timestamp(System.currentTimeMillis()))
        val count = math.min(_count, queryResultMaximumLimit)

        (
            ResourceTable.query
                .where(_.modified.between(since, until))
                .sortBy(_.modified asc)
                .drop(start)
                .take(count)
            ,
            IteratorData(since, until, start + count, count).generateId, // next
            IteratorData(since, until, start,         count).generateId  // current
        )
    }

    def listDataspacesQuery(_since: Option[Timestamp], _until: Option[Timestamp],
                            start: Int, _count: Int) = database withSession { implicit session: Session =>

        val since = _since.getOrElse(new Timestamp(0))
        val until = _until.getOrElse(new Timestamp(System.currentTimeMillis()))
        val count = math.min(_count, queryResultMaximumLimit)

        (
            DataspaceTable.query
                .where(_.isOrganization)
                .where(_.created.between(since, until))
                .sortBy(_.created asc)
                .drop(start)
                .take(count)
            ,
            IteratorData(since, until, start + count, count).generateId, // next
            IteratorData(since, until, start,         count).generateId  // current
        )
    }

    case class IteratorData(val since: Timestamp, val until: Timestamp, val start: Int, val count: Int) {
        def generateId: String = {
            val since = this.since.getTime
            val until = this.until.getTime

            val bytes = ByteBuffer.allocate(8 * 2 + 4 * 2)
                            .putLong(since)
                            .putLong(until)
                            .putInt(start)
                            .putInt(count)
                            .array

            IteratorData.base64encoder.encode(bytes)
        }
    }

    object IteratorData {
        val base64encoder = new BASE64Encoder()
        val base64decoder = new BASE64Decoder()

        def fromId(id: String) = Try {
            val raw = base64decoder.decodeBuffer(id)
            val bytes = ByteBuffer.wrap(raw)

            val since = bytes.getLong
            val until = bytes.getLong
            val start = bytes.getInt
            val count = bytes.getInt

            IteratorData(
                new Timestamp(since),
                new Timestamp(until),
                start,
                count)
        }
    }
}
