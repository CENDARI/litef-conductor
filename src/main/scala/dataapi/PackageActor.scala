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

import spray.http.HttpResponse
import spray.http._
import akka.actor.Actor
import ckan.CkanGodInterface
import MediaTypes._
import HttpCharsets._
import HttpMethods._
import HttpHeaders._
import spray.json._
import ckan.IteratorData
//import VisibilityFilter._
import scala.slick.lifted.Query
import slick.driver.PostgresDriver.simple._
//import akka.io.IO
//import akka.pattern.ask
import StateFilter._
import common.Config
import ckan.PackageJsonProtocol._

object PackageActor {
    case class ListPackages(
        val authorizationKey: String,
        val state: StateFilter,
        val start: Int = 0,
        val count: Int = CkanGodInterface.queryResultDefaultLimit
    )
    
    case class ListPackagesFromIterator(
        val authorizationKey: String,
        val iteratorData: String
    )
    
    case class ListDataspacePackages(
        val dataspaceId: String,
        val state: StateFilter,
        val start: Int = 0,
        val count: Int = CkanGodInterface.queryResultDefaultLimit
    )
    
    case class ListDataspacePackagesFromIterator(
        val dataspaceId: String,
        val iteratorData: String
    )
    
    case class GetPackageMetadata(
        val id: String
    )
}

class PackageActor extends Actor with dataapi.DefaultValues {
    import PackageActor._
    import context.system

    def receive: Receive = {
        case ListPackages(authorizationKey, state, start, count) =>
            CkanGodInterface.database withSession { implicit session: Session =>

            val (query, nextPage, currentPage) = CkanGodInterface.listPackagesQuery(authorizationKey, state, start, count)

            val packages = query.list
            val results = 
                if (packages.size > 0)
                    JsObject(
                        "nextPage"    -> JsString(nextPage map (s"${Config.namespace}sets/query/results/" + _)    getOrElse ""),
                        "currentPage" -> JsString(currentPage map (s"${Config.namespace}sets/query/results/" + _) getOrElse ""),
                        "data"        -> packages.toJson,
                        "end"         -> JsBoolean(false)
                    ).prettyPrint
                else
                    JsObject(
                        "end"         -> JsBoolean(true)
                    ).prettyPrint

            sender ! HttpResponse(status = StatusCodes.OK,
                                  entity = HttpEntity(ContentType(`application/json`, `UTF-8`), results))
            }

        case ListPackagesFromIterator(authorizationKey, iteratorData) =>
            val iterator = IteratorData.fromId(iteratorData).get
            receive(ListPackages(
                    authorizationKey,
                    iterator.state,
                    iterator.start,
                    iterator.count
                ))
        
        case ListDataspacePackages(dataspaceId, state, start, count) => 
            CkanGodInterface.database withSession { implicit session: Session =>
                
                val (query, nextPage, currentPage) = CkanGodInterface.listDataspacePackagesQuery(dataspaceId, state, start, count)
                val packages = query.list
                val results = 
                    if (packages.size > 0)
                        JsObject(
                            "nextPage"    -> JsString(nextPage map (s"${Config.namespace}dataspaces/$dataspaceId/sets/query/results/" + _)    getOrElse ""),
                            "currentPage" -> JsString(currentPage map (s"${Config.namespace}dataspaces/$dataspaceId/sets/query/results/" + _) getOrElse ""),
                            "data"        -> packages.toJson,
                            "end"         -> JsBoolean(false)
                        ).prettyPrint
                    else
                        JsObject(
                            "end"         -> JsBoolean(true)
                        ).prettyPrint
                    
                sender ! HttpResponse(status = StatusCodes.OK,
                                      entity = HttpEntity(ContentType(`application/json`, `UTF-8`), results))
            }
            
        case ListDataspacePackagesFromIterator(dataspaceId, iteratorData) => 
            val iterator = IteratorData.fromId(iteratorData).get
            receive(ListDataspacePackages(
                    dataspaceId,
                    iterator.state,
                    iterator.start,
                    iterator.count
                ))
          
        case GetPackageMetadata(id) =>
            CkanGodInterface.database withSession { implicit session: Session =>

                val result = CkanGodInterface.getPackage(id)
                result match { 
                  case Some(p) =>
                      sender ! HttpResponse(
                                  status = StatusCodes.OK,
                                  entity = HttpEntity(ContentType(`application/json`, `UTF-8`), 
                                                      p.toJson.prettyPrint))
                  case None =>
                      sender ! HttpResponse(StatusCodes.NotFound, s"""Set with id "$id" not found""")
                }
            }
        case response: HttpResponse =>
            println(s"Sending the response back to the requester $response")

        case other =>
            println(s"Found an unknown thing: $other")
            sender ! other
    }
}