/*
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

package dataapi

import akka.actor.ActorRef
import akka.pattern.ask
import scala.concurrent.ExecutionContext
import spray.routing._
import spray.http._
import dataapi.DataspaceRoleActor._
import spray.http.HttpResponse
import core.Core
import spray.json._
import DefaultJsonProtocol._
import CkanJsonProtocol._
import ckan.UserDataspaceRoleJsonProtocol._
import ckan.UserDataspaceRole
import common.Config.{ Ckan => CkanConfig }
import StateFilter._
import StateFilterProtocol._

// Needed for implicit conversions, not unused:
import scala.concurrent.ExecutionContext.Implicits.global
import reflect.ClassTag


class DataspaceRoleService()(implicit executionContext: ExecutionContext)
    extends CommonDirectives
{
    // TODO: Don't list all dataspace roles, but support iterators
    def listDataspaceRoles(userId: Option[String], dataspaceId: Option[String], state: StateFilter)(implicit authorizationKey: String) = complete {
        (Core.dataspaceRoleActor ? ListDataspaceRoles(authorizationKey, userId, dataspaceId, state))
        .mapTo[HttpResponse]
    }

    def getDataspaceRoleById(id: String)(implicit authorizationKey: String) =
        authorize(ckan.CkanGodInterface.isDataspaceRoleAccessibleToUser(id, authorizationKey)) {
            complete {
                (Core.dataspaceRoleActor ? GetDataspaceRoleById(id))
                .mapTo[HttpResponse]
            }
        }

    def createDataspaceRole(dataspaceRole: UserDataspaceRole) (implicit authorizationKey: String) = {
        complete {
            (Core.dataspaceRoleActor ? CreateDataspaceRole(authorizationKey, dataspaceRole))
            .mapTo[HttpResponse]
        }
    }

    def deleteDataspaceRole(id: String)(implicit authorizationKey: String) = {
        complete {
            (Core.dataspaceRoleActor ? DeleteDataspaceRole(authorizationKey, id))
            .mapTo[HttpResponse]
        }
    }

    val route = headerValueByName("Authorization") { implicit authorizationKey =>
        pathPrefix("privileges") {
                get {
                    pathEnd {
                        parameters('userId.as[String] ?, 'dataspaceId.as[String] ?, 'state.as[StateFilter]? StateFilter.ACTIVE) { (u, d, s) => listDataspaceRoles(u, d, s) }
                    } ~
                    path(Segment)   { getDataspaceRoleById }
                } ~
                post {
                    pathEnd {
                        entity(as[UserDataspaceRole])   { createDataspaceRole }
                    }
                } ~
                delete {
                    path(Segment)   { deleteDataspaceRole }
                }

            }
        }
}


