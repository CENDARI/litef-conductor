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
import dataapi.UserActor._
import spray.http.HttpResponse
import core.Core
import spray.json._
import DefaultJsonProtocol._
import CkanJsonProtocol._
import common.Config.{ Ckan => CkanConfig }

// Needed for implicit conversions, not unused:
import scala.concurrent.ExecutionContext.Implicits.global
import reflect.ClassTag


class UserService()(implicit executionContext: ExecutionContext)
    extends CommonDirectives
{
    // TODO: don't list all users, but support iterators
    def listUsers()(implicit authorizationKey: String) =
        authorize(ckan.CkanGodInterface.isRegisteredUser(authorizationKey)) {
            complete {
                (Core.userActor ? ListUsers())
                .mapTo[String]
            }
        }

    def getUserById(id: String)(implicit authorizationKey: String) =
        authorize(ckan.CkanGodInterface.isRegisteredUser(authorizationKey)) {
            complete {
                (Core.userActor ? GetUserById(id)).mapTo[HttpResponse]
            }
        }

    val route = headerValueByName("Authorization") { implicit authorizationKey =>
        pathPrefix("users") {
                get {
                    pathEnd         { listUsers() } ~
                    path(Segment)   { getUserById }
                }
            }
        }
}

