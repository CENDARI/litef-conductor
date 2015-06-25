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
import akka.io.IO
import akka.pattern.ask
import spray.can.Http
import spray.httpx.RequestBuilding._
import spray.httpx.SprayJsonSupport._
import StateFilter._
import common.Config. { Ckan => CkanConfig }
import common.Config
import ckan.PackageJsonProtocol._
//import dataapi.CkanJsonProtocol.CkanApiPackageCreateJsonFormat
//import dataapi.CkanJsonProtocol.CkanApiPackageUpdateWithIdJsonFormat
import dataapi.CkanJsonProtocol._
import scala.concurrent.ExecutionContext.Implicits.global

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
    
    case class CreatePackage(
        val authorizationKey: String,
        val pckg: CkanApiPackageCreate
    )
    
    case class UpdatePackage(
        val authorizationKey: String,
        val pckg: CkanApiPackageUpdateWithId
    )
}

class PackageActor extends Actor with dataapi.DefaultValues {
    import PackageActor._
    import context.system
    
    lazy val logger = org.slf4j.LoggerFactory getLogger getClass

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

                val result = CkanGodInterface.getPackageById(id)
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
            
        case CreatePackage(authorizationKey, pckg) => {
            val originalSender = sender
            
            (IO(Http) ? (Post(CkanConfig.namespace + "action/package_create", pckg)~>addHeader("Authorization", authorizationKey)))
            .mapTo[HttpResponse]
            .map { response => response.status match {
                    case StatusCodes.OK =>
                        // TODO: Try to read resource from (ugly) CKAN response, not from db
                        val res = CkanGodInterface.getPackageByName(pckg.name)
                        res match {
                            case Some(cp) =>
                                originalSender ! HttpResponse(status = StatusCodes.Created,
                                                              entity = HttpEntity(ContentType(`application/json`, `UTF-8`),cp.toJson.prettyPrint),
                                                              headers = List(Location(s"${Config.namespace}sets/${cp.id}")))
                            case None =>
                                originalSender ! HttpResponse(StatusCodes.InternalServerError, "Error reading newly created set from the database")
                        }
                        
                    case _ => 
                        logger error s"${response.entity}"
                        originalSender ! HttpResponse(response.status, "Error creating a set")
                    }
            }
        }
        case UpdatePackage(authorizationKey, pckg) => {
            
            val originalSender = sender
            
            // package_update removes package extras, resources, etc. if not supplied in the JSON
            // that's why we call package_show, replace title, description, ... and then call package_update
            (IO(Http) ? (Get(CkanConfig.namespace + s"action/package_show?id=${pckg.id}")~>addHeader("Authorization", authorizationKey)))
            .mapTo[HttpResponse]
            .map { response1 => 
                    val cr = JsonParser(response1.entity.asString).convertTo[CkanResponse]
                    (response1.status, cr.result) match {
                        case (StatusCodes.OK, Some(pckgOld)) =>
                            val pckgNew = new JsObject(pckgOld ++ pckg.toJson.asJsObject.fields)
                            // TODO: Check why pckgNew is not accepted (JsObject), but it has to be sent as String. This is spray related, not CKAN API related
                            (IO(Http) ? (Post(CkanConfig.namespace + "action/package_update", pckgNew.toString)~>addHeader("Authorization", authorizationKey)))
                            .mapTo[HttpResponse]
                            .map { response2 => response2.status match {
                                    case StatusCodes.OK =>
                                        val res = CkanGodInterface.getPackageById(pckg.id)
                                        res match {
                                            case Some(cp) =>
                                                originalSender ! HttpResponse(status = StatusCodes.OK,
                                                                              entity = HttpEntity(ContentType(`application/json`, `UTF-8`),cp.toJson.prettyPrint))
                                            case None =>
                                                originalSender ! HttpResponse(StatusCodes.InternalServerError, "Error reading the updated set from the database")
                                        }

                                    case _ => 
                                        logger error s"$response2"
                                        originalSender ! HttpResponse(response2.status, "Error updating the set")
                                    }
                            }
                        case (errorCode, errorMessage) =>
                            logger info s"$response1"
                            originalSender ! HttpResponse(errorCode, "Error getting set data from CKAN. The set cannot be updated")
                    }
            }
        }
        
        case response: HttpResponse =>
            println(s"Sending the response back to the requester $response")

        case other =>
            println(s"Found an unknown thing: $other")
            sender ! other
    }
}