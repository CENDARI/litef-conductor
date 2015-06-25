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
import scala.util.Try
import dataapi.StateFilter
import dataapi.StateFilter._
import dataapi.Visibility
import dataapi.Visibility._
import scala.slick.lifted.CanBeQueryCondition

// optionally filter on a column with a supplied predicate https://gist.github.com/cvogt/9193220
case class MaybeFilter[X, Y](val query: scala.slick.lifted.Query[X, Y]) {
  def filter[T,R:CanBeQueryCondition](data: Option[T])(f: T => X => R) = {
    data.map(v => MaybeFilter(query.filter(f(v)))).getOrElse(this)
  }
}

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
        ResourceTable.query.where(_.id === id)
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
     * @param authorizationKey CKAN authorization key of the user that requests resources
     * @param _since gets only the resources newer than the specified timestamp
     * @param _until gets only the resources older than the specified timestamp
     * @param state gets only the resources with the specified state
     * @param start (aka offset) skips first 'start' results
     * @param _count defines how many results to return
     * @return the query object against the collection of resources
     */
    def listResourcesQuery(authorizationKey: String, _since: Option[Timestamp], _until: Option[Timestamp], state: StateFilter,
                           start: Int, _count: Int) = database withSession { implicit session: Session =>

        val since = _since getOrElse (new Timestamp(0))
        val until = _until getOrElse (new Timestamp(System.currentTimeMillis()))
        val count = math.min(_count, queryResultMaximumLimit)
        
        var q = DataspaceResourceTable.query
                .filter(_.modified.between(since, until))
        
        if (state == StateFilter.ACTIVE || state == StateFilter.DELETED) q = q.filter(_.state === state.toString.toLowerCase)
        
        if(!isSysadmin(authorizationKey)) {
            val resourcesInDataspacesWithPrivileges =
                q.filter(_.dataspaceId in UserDataspaceRoleTable.query
                                        .filter(_.userApiKey === authorizationKey)
                                        .filter(_.state === "active")
                                        .map(_.dataspaceId))
            val resourcesInPublicDataspaces = 
                q.filter(_.dataspaceId in DataspaceExtraTable.query
                                        .filter(_.key === "visibility")
                                        .filter(_.value === "public")
                                        .filter(_.state === "active")
                                        .map(_.dataspaceId))
            q = resourcesInDataspacesWithPrivileges union resourcesInPublicDataspaces
                
        }
        q = q.sortBy(_.modified asc).drop(start).take(count)

        (
            q,
            Some(IteratorData(since, until, state, start + count, count).generateId), // next
            Some(IteratorData(since, until, state, start,         count).generateId)  // current
        )
    }
    
    def getPackageById(id: String): Option[Package] = database withSession { implicit session: Session =>
        database withSession { implicit session: Session =>
            PackageTable.query.filter(_.id === id).list.headOption
        }
    }
    
    def getPackageByName(name: String): Option[Package] = database withSession { implicit session: Session =>
        database withSession { implicit session: Session =>
            PackageTable.query.filter(_.name === name).list.headOption
        }
    }

    def listPackagesQuery(authorizationKey: String, state: StateFilter, start: Int, _count: Int) = database withSession { 
        implicit session: Session =>
        
        val since = new Timestamp(0)
        val until = new Timestamp(System.currentTimeMillis())
        val count = math.min(_count, queryResultMaximumLimit)
        
        var q = PackageTable.query
                .filter(_.modified.between(since, until))
        
        if (state == StateFilter.ACTIVE || state == StateFilter.DELETED) q = q.filter(_.state === state.toString.toLowerCase)        
        
        if(!isSysadmin(authorizationKey)) {
            val packagesInDataspacesWithPrivileges = q.filter(_.dataspaceId in UserDataspaceRoleTable.query
                                                                    .filter(_.userApiKey === authorizationKey)
                                                                    .filter(_.state === "active")
                                                                    .map(_.dataspaceId))
            val packagesInPublicDataspaces = q.filter(_.dataspaceId in DataspaceExtraTable.query
                                                            .filter(_.key === "visibility")
                                                            .filter(_.value === "public")
                                                            .filter(_.state === "active")
                                                            .map(_.dataspaceId))
                  
            q = packagesInDataspacesWithPrivileges union packagesInPublicDataspaces
        }
        
        q = q.sortBy( p => (p.dataspaceId, p.title asc)).drop(start).take(count)
        
        (
            q,
            Some(IteratorData(since, until, state, start + count, count).generateId), // next
            Some(IteratorData(since, until, state, start,         count).generateId)  // current
        )
    }
    
    def listDataspacePackagesQuery(dataspaceId: String, state: StateFilter, start: Int, _count: Int) = database withSession {
        implicit session: Session =>
        
        val since = new Timestamp(0)
        val until = new Timestamp(System.currentTimeMillis())
        val count = math.min(_count, queryResultMaximumLimit)
        
        var q = PackageTable.query
                .filter(_.dataspaceId === dataspaceId)
                .filter(_.modified.between(since, until))
                
        if (state == StateFilter.ACTIVE || state == StateFilter.DELETED) q = q.filter(_.state === state.toString.toLowerCase)
        
        q = q.sortBy(_.title asc).drop(start).take(count)
        
        (
            q,
            Some(IteratorData(since, until, state, start + count, count).generateId), // next
            Some(IteratorData(since, until, state, start,         count).generateId)  // current
        )
    }
    /**
     * @param authorizationKey CKAN authorization key of the user that requests the dataspaces
     * @param _since gets only the dataspaces newer than the specified timestamp
     * @param _until gets only the dataspaces older than the specified timestamp
     * @param state
     * @param visibility
     * @param start (aka offset) skips first 'start' results
     * @param _count defines how many results to return
     * @return the query object against the collection of dataspaces
     */
    def listDataspacesQuery(authorizationKey: String,
                            _since: Option[Timestamp], _until: Option[Timestamp],
                            state: StateFilter, visibility: Option[Visibility],
                            start: Int, _count: Int
                            ) = database withSession { implicit session: Session =>

        val since = _since getOrElse (new Timestamp(0))
        val until = _until getOrElse (new Timestamp(System.currentTimeMillis()))
        val count = math.min(_count, queryResultMaximumLimit)

        var q = DataspaceTable.query
                .filter(_.isOrganization)
                .filter(_.created.between(since, until))

        if (state == StateFilter.ACTIVE || state == StateFilter.DELETED) q = q.filter(_.state === state.toString.toLowerCase)
        
        val isAdmin = isSysadmin(authorizationKey)
        
        visibility match {
            case Some(Visibility.PRIVATE) =>
                q = q.filterNot(_.id in DataspaceExtraTable.query
                                        .filter(_.key === "visibility")
                                        .filter(_.value === "public")
                                        .filter(_.state === "active")
                                        .map(_.dataspaceId))
                if(!isAdmin) {
                    q = q.filter(_.id in UserDataspaceRoleTable.query
                                            .filter(_.userApiKey === authorizationKey)
                                            .filter(_.state === "active")
                                            .map(_.dataspaceId))
                }
            case Some(Visibility.PUBLIC) =>
                q = q.filter(_.id in DataspaceExtraTable.query
                                        .filter(_.key === "visibility")
                                        .filter(_.value === "public")
                                        .filter(_.state === "active")
                                        .map(_.dataspaceId))
            case _ =>
                if (!isAdmin) {
                    val queryDataspacesWithPrivileges = q.filter(_.id in UserDataspaceRoleTable.query
                                                                        .filter(_.userApiKey === authorizationKey)
                                                                        .filter(_.state === "active")
                                                                        .map(_.dataspaceId))
                    val queryDataspacesPublic = q.filter(_.id in DataspaceExtraTable.query
                                                                    .filter(_.key === "visibility")
                                                                    .filter(_.value === "public")
                                                                    .filter(_.state === "active")
                                                                    .map(_.dataspaceId))
                    q = queryDataspacesWithPrivileges union queryDataspacesPublic
                }
            
        }
        
        q = q.sortBy(_.title asc)
        
        (
            q,
            None, // Dataspaces do not support iterators // IteratorData(since, until, start + count, count).generateId, // next
            None  // Dataspaces do not support iterators // IteratorData(since, until, start,         count).generateId  // current
        )
    }


    /**
     * @param id UUID of the desired resource
     * @return query object that will return the specified dataspace
     */
    def getDataspaceQuery(id: String) = database withSession { implicit session: Session =>
        DataspaceTable.query.filter(_.id === id)
    }

    /**
     * @param id UUID of the desired resource
     * @return information for the specified dataspace
     */
    def getDataspace(id: String) = {
        database withSession { implicit session: Session =>
            getDataspaceQuery(id).list.headOption
        }
    }
    
    def getDataspaceExtrasQuery(id: String) = {
        database withSession { implicit session: Session =>
            DataspaceExtraTable.query
            .filter(_.dataspaceId === id)
            .filter(_.state === "active")
        }
    }
    
    def getDataspaceExtras(id: String) = {
        database withSession { implicit session: Session =>
            getDataspaceExtrasQuery(id).list
        }
    }
    /**
     * @param dataspaceId UUID of the requested dataspace
     * @param _since gets only the resources newer than the specified timestamp
     * @param _until gets only the resources older than the specified timestamp
     * @param state
     * @param start (aka offset) skips first 'start' results
     * @param _count defines how many results to return
     * @return query object for resources belonging to the specified dataspace
     */
    def listDataspaceResourcesQuery(
                            // already authorized // authorizationKey: String,
                            dataspaceId: String,
                            _since: Option[Timestamp], _until: Option[Timestamp],
                            state: StateFilter,
                            start: Int, _count: Int
                            ) = database withSession { implicit session: Session =>

        val since = _since getOrElse (new Timestamp(0))
        val until = _until getOrElse (new Timestamp(System.currentTimeMillis()))
        val count = math.min(_count, queryResultMaximumLimit)

        var query = DataspaceResourceTable.query
                    .filter(_.dataspaceId === dataspaceId)
                    .filter(_.modified.between(since, until))

        if(state == StateFilter.ACTIVE || state == StateFilter.DELETED) query = query.filter(_.state === state.toString.toLowerCase)

        query = query.sortBy(_.modified asc)
                .drop(start)
                .take(count)

        (
            query,
            Some(IteratorData(since, until, state, start + count, count).generateId), // next
            Some(IteratorData(since, until, state, start,         count).generateId)  // current
        )
    }
    
    /**
     * @param packageId UUID of the requested package
     * @param _since gets only the resources newer than the specified timestamp
     * @param _until gets only the resources older than the specified timestamp
     * @param state
     * @param start (aka offset) skips first 'start' results
     * @param _count defines how many results to return
     * @return query object for resources belonging to the specified package
     */
    def listPackageResourcesQuery(
                            // already authorized // authorizationKey: String,
                            packageId: String,
                            _since: Option[Timestamp], _until: Option[Timestamp],
                            state: StateFilter,
                            start: Int, _count: Int
                            ) = database withSession { implicit session: Session =>

        val since = _since getOrElse (new Timestamp(0))
        val until = _until getOrElse (new Timestamp(System.currentTimeMillis()))
        val count = math.min(_count, queryResultMaximumLimit)

        var query = DataspaceResourceTable.query
                    .filter(_.packageId === packageId)
                    .filter(_.modified.between(since, until))

        if(state == StateFilter.ACTIVE || state == StateFilter.DELETED) query = query.filter(_.state === state.toString.toLowerCase)

        query = query.sortBy(_.modified asc)
                .drop(start)
                .take(count)

        (
            query,
            Some(IteratorData(since, until, state, start + count, count).generateId), // next
            Some(IteratorData(since, until, state, start,         count).generateId)  // current
        )
    }

    /**
     * @param dataspaceId dataspace UUID
     * @param packageId package UUID
     * @return whether the package belongs to the dataspace
     */
    def isPackageInDataspace(dataspaceId: String, packageId: String): Boolean = database withSession { implicit session: Session =>
        PackageTable.query
            .filter(_.id === packageId)
            .filter(_.dataspaceId === dataspaceId)
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
        if(isSysadmin(authorizationKey)) true
        else if(isDataspacePublic(id)) true
        else UserDataspaceRoleTable.query
                .filter(_.dataspaceId === id)
                .filter(_.userApiKey === authorizationKey)
                .filter(_.state === "active")
                .take(1)
                .list
                .size > 0
    }
    
    def isPackageAccessibleToUser(id: String, authorizationKey: String): Boolean = database withSession {implicit session: Session =>
        if(isSysadmin(authorizationKey)) true
        else if(isPackageInPublicDataspace(id)) true 
        else PackageTable.query
                .map(_.justIds)
                .filter(_._2 === id)
                .filter(_._1 in UserDataspaceRoleTable.query
                                        .filter(_.userApiKey === authorizationKey)
                                        .filter(_.state === "active")
                                        .map(_.dataspaceId))
                .take(1)
                .list
                .size > 0
    }
    
    def isPackageModifiableByUser(id: String, authorizationKey: String): Boolean = database withSession {implicit session: Session =>
        if(isSysadmin(authorizationKey))
            true
        else PackageTable.query
                .map(_.justIds)
                .filter(_._2 === id)
                .filter(_._1 in UserDataspaceRoleTable.query
                                        .filter(_.userApiKey === authorizationKey)
                                        .filter(_.dataspaceRole inSet List("admin", "editor"))
                                        .filter(_.state === "active")
                                        .map(_.dataspaceId))
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
        if(isSysadmin(authorizationKey))
            true
        else UserDataspaceRoleTable.query
                .filter(_.dataspaceId === id)
                .filter(_.userApiKey === authorizationKey)
                .filter(_.dataspaceRole inSet List("admin", "editor"))
                .filter(_.state === "active")
                .take(1)
                .list
                .size > 0
    }

     /**
     * @param id resource UUID
     * @param authorizationKey CKAN user authorization id
     * @return whether the resource can be modifiable by user
     */
    def isResourceModifiableByUser(id: String, authorizationKey: String): Boolean = database withSession { implicit session: Session =>
        if(isSysadmin(authorizationKey))
            true
        else
            DataspaceResourceTable.query
            .map(_.justIds)
            .filter(_._2 === id)
            .filter(_._1 in UserDataspaceRoleTable.query
                               .filter(_.userApiKey === authorizationKey)
                               .filter(_.dataspaceRole inSet List("admin", "editor"))
                               .filter(_.state === "active")
                               .map(_.dataspaceId))
            .take(1)
            .list
            .size > 0
    }
    
    /**
     * @param id resource UUID
     * @param authorizationKey CKAN user authorization id
     * @return whether the resource can be deleted by user
     */
    def isDataspaceDeletableByUser(id: String, authorizationKey: String): Boolean = database withSession { implicit session: Session =>
        if(isSysadmin(authorizationKey))
            true
        else UserDataspaceRoleTable.query
            .filter(_.dataspaceId === id)
            .filter(_.userApiKey === authorizationKey)
            .filter(_.dataspaceRole === "admin")
            .filter(_.state === "active")
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
        if(isSysadmin(authorizationKey)) true
        else if(isResourceInPublicDataspace(id)) true
        else DataspaceResourceTable.query
                .map(_.justIds)
                .filter(_._2 === id)
                .filter(_._1 in UserDataspaceRoleTable.query
                                   .filter(_.userApiKey === authorizationKey)
                                   .filter(_.state === "active")
                                   .map(_.dataspaceId))
                .take(1)
                .list
                .size > 0
    }

    /**
     * @param authorizationKey CKAN user authorization key
     * @return whether there is a CKAN user with specified authorization key
     */
    def isRegisteredUser(authorizationKey: String): Boolean = database withSession { implicit session: Session =>
        UserTable.query
          .filter(_.apikey === authorizationKey)
          .filter(_.state === "active")
          .take(1)
          .list
          .size > 0
    }
    
    /**
     * @param authorizationKey CKAN user authorization key
     * @return whether the user with specified authorization key is a CKAN admin
     */
    def isSysadmin(authorizationKey: String): Boolean = database withSession { implicit session: Session =>
        UserTable.query
        .filter(_.apikey === authorizationKey)
        .filter(_.sysadmin)
        .filter(_.state === "active")
        .take(1)
        .list
        .size > 0
    }

    def isDataspacePublic(id: String): Boolean = database withSession { implicit session: Session =>
        DataspaceExtraTable.query
        .filter(_.dataspaceId === id)
        .filter(_.key === "visibility")
        .filter(_.value === "public")
        .filter(_.state === "active")
        .take(1)
        .list
        .size > 0
    }
    
    def isPackageInPublicDataspace(id: String): Boolean = database withSession { implicit session: Session =>
        PackageTable.query
            .map(_.justIds)
            .filter(_._2 === id)
            .filter(_._1 in DataspaceExtraTable.query
                                .filter(_.key === "visibility")
                                .filter(_.value === "public")
                                .filter(_.state === "active")
                                .map(_.dataspaceId))
            .take(1)
            .list
            .size > 0
    }
    
    def isResourceInPublicDataspace(id: String): Boolean = database withSession { implicit session: Session =>
        DataspaceResourceTable.query
            .map(_.justIds)
            .filter(_._2 === id)
            .filter(_._1 in DataspaceExtraTable.query
                                .filter(_.key === "visibility")
                                .filter(_.value === "public")
                                .filter(_.state === "active")
                                .map(_.dataspaceId))
            .take(1)
            .list
            .size > 0
    }
    /**
     * @return list of CKAN users (default CKAN users omitted)
     */
    def listUsers() = database withSession { implicit session: Session =>
        UserTable.query
        .filterNot(_.username inSet List("logged_in", "visitor", "default"))
        .filter(_.state === "active")
        .list
    }

    def getUserById(id: String) = database withSession { implicit session: Session =>
        UserTable.query
        .filter(_.id === id)
        .filterNot(_.username inSet List("logged_in", "visitor", "default"))
        .filter(_.state === "active")
        .list
        .headOption
    }

    def getUserByOpenId(openId: String) = database withSession { implicit session: Session =>
        UserTable.query
        .filter(_.openid === openId)
        .filterNot(_.username inSet List("logged_in", "visitor", "default"))
        .filter(_.state === "active")
        .list
        .headOption
    }

    // TODO: (General) List can be empty for one of two reasons:
    // 1) the requested resource does not exists
    // 2) the user is not authorized to access the resource
    // Distinguish these two and send appropriate responses.
    // Currently 403 (Not authorized) is sent in both cases.
    def isDataspaceRoleAccessibleToUser(id: String, authorizationKey: String) = database withSession { implicit session: Session =>
        var q = UserDataspaceRoleTable.query
                .filter(_.id === id)
                .filter(_.state === "active")
        
        if(!isSysadmin(authorizationKey))
          q = q.filter(_.dataspaceId in UserDataspaceRoleTable.query
                                        .filter(_.userApiKey === authorizationKey)
                                        .filter(_.state === "active")
                                        .map(_.dataspaceId))
        q.take(1).list.size > 0
    }

    def listDataspaceRoles(authorizationKey: String, userId: Option[String], dataspaceId: Option[String], state: StateFilter) =
        database withSession { implicit session: Session =>
            var q = MaybeFilter(UserDataspaceRoleTable.query)
                        .filter(userId)(u => r => r.userId === u)
                        .filter(dataspaceId)(d => r => r.dataspaceId === d)
                        .query
                        
            if(!isSysadmin(authorizationKey))
              q = q.filter(_.dataspaceId in UserDataspaceRoleTable.query
                                            .filter(_.userApiKey === authorizationKey)
                                            .filter(_.state === "active")
                                            .map(_.dataspaceId))
            if (state == StateFilter.ACTIVE || state == StateFilter.DELETED) q = q.filter(_.state === state.toString.toLowerCase)

            q.list
    }

    def getDataspaceRoleById(id: String) = database withSession { implicit session: Session =>
        UserDataspaceRoleTable.query
        .filter(_.id === id)
        .filter(_.state === "active")
        .list
        .headOption
    }

    def getDataspaceRoleByUserDataspaceAndRole(userId: String, dataspaceId: String, dataspaceRole: String) =
        database withSession { implicit session: Session =>
            UserDataspaceRoleTable.query
            .filter(_.userId === userId)
            .filter(_.dataspaceId === dataspaceId)
            .filter(_.dataspaceRole === dataspaceRole)
            .filter(_.state === "active")
            .list
            .headOption
        }
}
