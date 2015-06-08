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

import scala.concurrent.ExecutionContext
import dataapi.PackageActor._
import core.Core.packageActor
import StateFilter._
import StateFilterProtocol._
//import akka.actor.ActorRef
import akka.pattern.ask
import spray.http.HttpResponse

class PackageService()(implicit executionContext: ExecutionContext)
    extends CommonDirectives
{
    def listPackages(dataspaceId: Option[String], state: StateFilter)(implicit authorizationKey: String) = {
        dataspaceId match {
            case Some(did) =>
                // TODO: return 404 if there is no dataspace with the specified id
                authorize(ckan.CkanGodInterface.isDataspaceAccessibleToUser(did, authorizationKey)) {
                    complete {
                        (packageActor ? ListDataspacePackages(did, state)).mapTo[HttpResponse]
                    }
                }
            case None =>
                authorize(ckan.CkanGodInterface.isRegisteredUser(authorizationKey))
                    complete {
                        (packageActor ? ListPackages(authorizationKey, state)).mapTo[HttpResponse]
                    }
        }
    }
    
    def listPackagesFromIterator(iteratorData: String)(implicit authorizationKey: String) =
        complete {
            (packageActor ? ListPackagesFromIterator(authorizationKey, iteratorData)).mapTo[HttpResponse]
        }
    
    val route = headerValueByName("Authorization") { implicit authorizationKey =>
        pathPrefix("sets") {
            get {
                pathEnd {
                    parameters('dataspaceId.as[String] ?,
                               'state.as[StateFilter] ? StateFilter.ACTIVE) 
                    { listPackages }
                } ~
                path("query" / "results" / Segment) {listPackagesFromIterator}
            }
        }
    }
}