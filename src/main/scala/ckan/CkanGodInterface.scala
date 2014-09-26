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


    /**
     * @param id UUID of the desired resource
     * @return query object that will return the specified resource
     */
    def getResourceQuery(id: String) = database withSession { implicit session: Session =>
        ResourceTable.query.filter(_.id === id)
    }

    /**
     * @param id UUID of the desired resource
     * @return returns the information for the resource specified by UUID, if it exists
     */
    def getResource(id: String): Option[Resource] = {
        database withSession { implicit session: Session =>
            getResourceQuery(id).list.headOption
        }
    }

    /**
     * @param id UUID of the desired resource
     * @return the URL for the specified resource
     */
    def getResourceUrl(id: String): Option[String] = database withSession { implicit session: Session =>
        getResourceQuery(id)
            .map(_.url)
            .list
            .headOption
    }

    /**
     * @param _since gets only the resources newer than the specified timestamp
     * @param _until gets only the resources older than the specified timestamp
     * @param start (aka offset) skips first 'start' results
     * @param _count defines how many results to return
     * @return the query object against the collection of resources
     */
    def listResourcesQuery(_since: Option[Timestamp], _until: Option[Timestamp],
                           start: Int, _count: Int) = database withSession { implicit session: Session =>

        val since = _since getOrElse (new Timestamp(0))
        val until = _until getOrElse (new Timestamp(System.currentTimeMillis()))
        val count = math.min(_count, queryResultMaximumLimit)

        (
            ResourceTable.query
                .filter(_.modified.between(since, until))
                .sortBy(_.modified asc)
                .drop(start)
                .take(count)
            ,
            Some(IteratorData(since, until, start + count, count).generateId), // next
            Some(IteratorData(since, until, start,         count).generateId)  // current
        )
    }

    /**
     * @param authorizationKey CKAN authorization key of the user that requests the dataspaces
     * @param _since gets only the dataspaces newer than the specified timestamp
     * @param _until gets only the dataspaces older than the specified timestamp
     * @param start (aka offset) skips first 'start' results
     * @param _count defines how many results to return
     * @return the query object against the collection of dataspaces
     */
    def listDataspacesQuery(authorizationKey: String,
                            _since: Option[Timestamp], _until: Option[Timestamp],
                            start: Int, _count: Int
                            ) = database withSession { implicit session: Session =>

        val since = _since getOrElse (new Timestamp(0))
        val until = _until getOrElse (new Timestamp(System.currentTimeMillis()))
        val count = math.min(_count, queryResultMaximumLimit)

        (
            DataspaceTable.query
                .filter(_.isOrganization)
                .filter(_.id in UserDataspaceRoleTable.query
                    .filter { _.userApiKey === authorizationKey }
                    .map   { _.dataspaceId }
                )
                .sortBy(_.title asc)
            ,
            None, // Dataspaces do not support iterators // IteratorData(since, until, start + count, count).generateId, // next
            None  // Dataspaces do not support iterators // IteratorData(since, until, start,         count).generateId  // current
        )
    }


    /**
     * @param authorizationKey CKAN authorization key of the user that requests the dataspaces
     * @param id UUID of the desired resource
     * @return query object that will return the specified dataspace
     */
    def getDataspaceQuery(authorizationKey: String, id: String) = database withSession { implicit session: Session =>
        DataspaceTable.query
            .filter(_.id === id)
            .filter(_.id in UserDataspaceRoleTable.query
                .filter { _.userApiKey === authorizationKey }
                .map   { _.dataspaceId }
            )
    }

    /**
     * @param authorizationKey CKAN authorization key of the user that requests the dataspaces
     * @param id UUID of the desired resource
     * @return information for the specified dataspace
     */
    def getDataspace(authorizationKey: String, id: String) = {
        database withSession { implicit session: Session =>
            getDataspaceQuery(authorizationKey, id).list.headOption
        }
    }


    /**
     * @param dataspaceId UUID of the requested dataspace
     * @param _since gets only the resources newer than the specified timestamp
     * @param _until gets only the resources older than the specified timestamp
     * @param start (aka offset) skips first 'start' results
     * @param _count defines how many results to return
     * @return query object for resources belonging to the specified dataspace
     */
    def listDataspaceResourcesQuery(
                            // already authorized // authorizationKey: String,
                            dataspaceId: String,
                            _since: Option[Timestamp], _until: Option[Timestamp],
                            start: Int, _count: Int
                            ) = database withSession { implicit session: Session =>

        val since = _since getOrElse (new Timestamp(0))
        val until = _until getOrElse (new Timestamp(System.currentTimeMillis()))
        val count = math.min(_count, queryResultMaximumLimit)

        (
            DataspaceResourceTable.query
                .filter(_.dataspaceId === dataspaceId)
                .filter(_.modified.between(since, until))
                .sortBy(_.modified asc)
                .drop(start)
                .take(count)
            ,
            Some(IteratorData(since, until, start + count, count).generateId), // next
            Some(IteratorData(since, until, start,         count).generateId)  // current
        )
    }

    /**
     * @param dataspaceId dataspace UUID
     * @param packageId package UUID
     * @return whether the package belongs to the dataspace
     */
    def isPackageInDataspace(dataspaceId: String, packageId: String): Boolean = database withSession { implicit session: Session =>
        PackageTable.query
            .filter(p => p.id === packageId && p.state === "active" && p.ownerOrg === dataspaceId)
            .take(1)
            .list
            .size > 0
    }

    /**
     * @param id dataspace UUID
     * @param authorizationKey CKAN user authorization id
     * @return whether the dataspace is accessible to the user
     */
    def isDataspaceAccessibleToUser(id: String, authorizationKey: String): Boolean = database withSession { implicit session: Session =>
        UserDataspaceRoleTable.query
            .filter(_.dataspaceId === id)
            .filter(_.userApiKey === authorizationKey)
            .take(1)
            .list
            .size > 0
    }

    /**
     * @param id dataspace UUID
     * @param authorizationKey CKAN user authorization id
     * @return whether the dataspace is modifiable to the user
     */
    def isDataspaceModifiableByUser(id: String, authorizationKey: String): Boolean = database withSession { implicit session: Session =>
        UserDataspaceRoleTable.query
            .filter(ds =>
                ds.dataspaceId === id &&
                ds.userApiKey === authorizationKey &&
                (ds.dataspaceRole === "editor" || ds.dataspaceRole === "admin"))
            .take(1)
            .list
            .size > 0
    }

    /**
     * @param id resource UUID
     * @param authorizationKey CKAN user authorization id
     * @return whether the resource is accessible to the user
     */
    def isResourceAccessibleToUser(id: String, authorizationKey: String): Boolean = database withSession { implicit session: Session =>
        DataspaceResourceTable.query
            .map(_.justIds)
            .filter(_._2 === id)
            .filter(_._1 in UserDataspaceRoleTable.query
                               .filter(_.userApiKey === authorizationKey)
                               .map(_.dataspaceId))
            .take(1)
            .list
            .size > 0
    }


    /**
     * A convenience class to manage the chunked responses without explicitely
     * exposing the start/offset mechanism
     * @param since
     * @param until
     * @param start
     * @param count
     */
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
