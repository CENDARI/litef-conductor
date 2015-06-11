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
import dataapi.CkanJsonProtocol.CkanApiPackageCreateJsonFormat
import dataapi.CkanJsonProtocol.CkanApiPackageUpdateJsonFormat
import dataapi.CkanJsonProtocol.CkanApiPackageUpdateWithIdJsonFormat

//import spray.json._
//import spray.http._
//import MediaTypes._
//import HttpCharsets._
//import HttpMethods._
//import HttpHeaders._

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
    
    def getPackageMetadata(id: String) (implicit authorizationKey: String) =
        authorize(ckan.CkanGodInterface.isPackageAccessibleToUser(id, authorizationKey)) {
            complete {
                (packageActor ? GetPackageMetadata(id)).mapTo[HttpResponse]
            }
        }
    
    def createPackage(pckg: CkanApiPackageCreate) (implicit authorizationKey: String) = {
        authorize(ckan.CkanGodInterface.isDataspaceModifiableByUser(pckg.dataspaceId, authorizationKey)) {
            complete {
                (packageActor ? CreatePackage(authorizationKey, pckg)).mapTo[HttpResponse]
            }
        }
    }
        
    def updatePackage(id: String, pckg: CkanApiPackageUpdate) (implicit authorizationKey: String) = {
        authorize(ckan.CkanGodInterface.isPackageModifiableByUser(id, authorizationKey)) {
            complete {
                (packageActor ? UpdatePackage(authorizationKey, CkanApiPackageUpdateWithId(id, pckg.title, pckg.description, pckg.isPrivate)))
                .mapTo[HttpResponse]
            }
        }
    }
    
    val route = headerValueByName("Authorization") { implicit authorizationKey =>
        pathPrefix("sets") {
            get {
                pathEnd {
                    parameters('dataspaceId.as[String] ?,
                               'state.as[StateFilter] ? StateFilter.ACTIVE) 
                    { listPackages }
                } ~
                path("query" / "results" / Segment) {listPackagesFromIterator} ~
                path(Segment) {getPackageMetadata}
            } ~
            post {
                pathEnd { entity(as[CkanApiPackageCreate]) { createPackage } }
            } ~
            put {
                (path(Segment) & entity(as[CkanApiPackageUpdate])) { updatePackage }
            }
        }
    }
}