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

package dataapi

import slick.driver.PostgresDriver.simple._

import akka.actor.Actor
import akka.io.IO
import akka.pattern.ask
import spray.can.Http
import spray.http._
import spray.httpx.RequestBuilding._
import MediaTypes._
import HttpCharsets._
import HttpMethods._
import HttpHeaders._
import StateFilter._
import Visibility._
import common.Config.{ Ckan => CkanConfig }
import common.Config

import scala.concurrent.ExecutionContext.Implicits.global
import spray.http.HttpResponse
import java.sql.Timestamp
import ckan.{CkanGodInterface, DataspaceTable}
import ckan.DataspaceJsonProtocol._
import ckan.ResourceJsonProtocol._
import CkanJsonProtocol._
import scala.slick.lifted.{Column, Query}
import spray.http.HttpHeaders.Location
import ckan.IteratorData
import java.util.UUID
import ckan.DataspaceWithExtras

object DataspaceActor {
    /// Gets the list of resources modified in the specified time range
    case class ListDataspaces(
            val authorizationKey: String,
            val since: Option[Timestamp],
            val until: Option[Timestamp],
            val state: StateFilter,
            val visibility: Option[Visibility],
            val origin: Option[String] = None,
            val start: Int = 0,
            val count: Int = CkanGodInterface.queryResultDefaultLimit
        )

//    /// Gets the next results for the iterator
//    case class ListDataspacesFromIterator(
//            val authorizationKey: String,
//            val iterator: String
//        )

    /// Gets the meta data for the the specified resource
    case class GetDataspaceMetadata(
            val id: String)

    /// Creates a dataspace
    case class CreateDataspace(
            val authorizationKey: String,
            val dataspace: DataspaceCreateWithId
          )

    /// Updates a dataspace
    case class UpdateDataspace(
            val authorizationKey: String,
            val dataspace: DataspaceUpdateWithId)

    /// Creates a new package in the dataspace
    case class CreatePackageInDataspace(
            val authorizationKey: String,
            val p: PackageCreateWithId)

    case class DeleteDataspace(
       val authorizationKey: String,
       val id: String
    )
}

class DataspaceActor
    extends Actor
    with dataapi.DefaultValues
{
    import DataspaceActor._
    import context.system
    import spray.json._
    import DefaultJsonProtocol._
    import spray.httpx.SprayJsonSupport._
    
    lazy val logger = org.slf4j.LoggerFactory getLogger getClass
    
    def postRequest[T](action: String, data: T, authorizationKey: String)(implicit evidence: spray.httpx.marshalling.Marshaller[T]) =
        (IO(Http) ? (
            Post(CkanConfig.namespace + "action/" + action, data)~>addHeader("Authorization", authorizationKey)
        ))
        
    def dataspaceExtrasToMap(extras: List[ckan.DataspaceExtra]): Map[String, String] = {
        val t = for {
            ckan.DataspaceExtra(_, _, Some(k), Some(v), _, _) <- extras
        } yield (k, v)
        t.toMap
    }
    
    def receive: Receive = {
        /// Gets the list of resources modified in the specified time range
        case ListDataspaces(authorizationKey, since, until, state, visibility, origin, start, count) =>
            CkanGodInterface.database withSession { implicit session: Session =>
                val (dataspacesQuery, nextPage, currentPage) =
                    CkanGodInterface.listDataspacesQuery(authorizationKey, since, until, state, visibility, origin, start, count)

                val dataspacesList = dataspacesQuery.list
                
                val dataspacesWithExtrasList = for {
                    d <- dataspacesList
                    extras = CkanGodInterface.getDataspaceExtrasQuery(d.id).list
                    extrasMap = dataspaceExtrasToMap(extras)
                } yield DataspaceWithExtras(d, extrasMap)
                
                // Dataspaces do not support iterators thanks to CKAN //
                // "nextPage"    -> JsString(nextPage.map("/resources/query/results/" + _)    getOrElse ""),
                // "currentPage" -> JsString(currentPage.map("/resources/query/results/" + _) getOrElse ""),

                sender ! HttpResponse(status = StatusCodes.OK,
                                      entity = HttpEntity(ContentType(`application/json`, `UTF-8`),
                                                          JsObject("data" -> dataspacesWithExtrasList.toJson).prettyPrint))
            }

//        /// Decodes the iterator data and invokes ListDataspaces
//        case ListDataspacesFromIterator(authorizationKey, iteratorData) =>
//            val iterator = IteratorData.fromId(iteratorData).get
//            receive(ListDataspaces(
//                authorizationKey,
//                Some(iterator.since),
//                Some(iterator.until),
//                iterator.state,
//                iterator.start,
//                iterator.count
//            ))

        /// Gets the meta data for the the specified resource
        case GetDataspaceMetadata(id) =>
            CkanGodInterface.database withSession { implicit session: Session =>

                val dataspace = CkanGodInterface.getDataspace(id)
                
                dataspace match {
                    case Some(ds) =>
                        val extras = CkanGodInterface.getDataspaceExtrasQuery(ds.id).list
                        val extrasMap = dataspaceExtrasToMap(extras)
                        val dataspaceWithExtras = DataspaceWithExtras(ds, extrasMap)
                        sender ! HttpResponse(
                            status = StatusCodes.OK, 
                            entity = HttpEntity(
                                ContentType(`application/json`, `UTF-8`),
                                dataspaceWithExtras.toJson.prettyPrint))
                    case None =>
                        sender ! HttpResponse(StatusCodes.NotFound, "The requested dataspace could not be found")
                }
            }

        // Creates a new dataspace
        case CreateDataspace(authorizationKey, dataspace) => {
            val originalSender = sender

            // Passing the request to CKAN
            postRequest("organization_create", dataspace, authorizationKey)
                .mapTo[HttpResponse]
                .map { response => response.status match {
                    case StatusCodes.OK =>
                        // TODO: Try to read dataspace from (ugly) CKAN response, not from db
                        val createdDataspace = CkanGodInterface.getDataspace(dataspace.id)
                        createdDataspace match {
                            case Some(ds) =>
                                val extras = CkanGodInterface.getDataspaceExtras(ds.id)
                                val extrasMap = dataspaceExtrasToMap(extras)
                                val dataspaceWithExtras = DataspaceWithExtras(ds, extrasMap)
                                originalSender ! HttpResponse(
                                    status = StatusCodes.Created, 
                                    entity = HttpEntity(
                                        ContentType(`application/json`, `UTF-8`),
                                        dataspaceWithExtras.toJson.prettyPrint),
                                        headers = List(Location(s"${common.Config.namespace}dataspaces/${ds.id}")))
                            case None => originalSender ! HttpResponse(StatusCodes.InternalServerError, "Error reading newly created dataspace metadata from the database")
                        }

                    case _ =>
                        logger info s"$response"
                        //originalSender ! response
                        originalSender ! HttpResponse(response.status, "Error creating dataspace!")
                }
            }
        }

        // Updates the dataspace
        case UpdateDataspace(authorizationKey, dataspace) => {
            val originalSender = sender
            
            // organization_update removes organization users info if not supplied in the JSON
            // that's why we call organization_show, replace title, description, ... and then call organization_update
            (IO(Http) ? (Get(CkanConfig.namespace + s"action/organization_show?id=${dataspace.id}&include_datasets=false")~>addHeader("Authorization", authorizationKey)))
            .mapTo[HttpResponse]
            .map { response1 => 
                    val cr = JsonParser(response1.entity.asString).convertTo[CkanResponse]
                    (response1.status, cr.result) match {
                        case (StatusCodes.OK, Some(dsOld)) =>
                            val extrasOldList = dsOld get "extras" match {
                                case Some(x: JsArray) =>  x.convertTo[List[CkanApiExtras]]
                                case _ => List()
                            }
                            val extrasNewList = dataspace.toJson.asJsObject.fields get "extras" match {
                                case Some(x: JsArray) =>  x.convertTo[List[CkanApiExtras]]
                                case _ => List()
                            }
                            val extras = extrasOldList ::: extrasNewList
                            val dsNew = JsObject(dsOld ++ dataspace.toJson.asJsObject.fields ++ JsObject("extras" -> extras.toJson).fields)
                            
                            // TODO: Check why dsNew is not accepted (JsObject), but it has to be sent as String. This is spray related, not CKAN API related
                            (IO(Http) ? (Post(CkanConfig.namespace + "action/organization_update", dsNew.toString)~>addHeader("Authorization", authorizationKey)))
                            .mapTo[HttpResponse]
                            .map { response2 => response2.status match {
                                    case StatusCodes.OK =>
                                        val res = CkanGodInterface.getDataspace(dataspace.id)
                                        res match {
                                            case Some(cd) =>
                                                val extras = CkanGodInterface.getDataspaceExtras(cd.id)
                                                val extrasMap = dataspaceExtrasToMap(extras)
                                                val dataspaceWithExtras = DataspaceWithExtras(cd, extrasMap)
                                                originalSender ! HttpResponse(status = StatusCodes.OK,
                                                                              entity = HttpEntity(ContentType(`application/json`, `UTF-8`),dataspaceWithExtras.toJson.prettyPrint))
                                            case None =>
                                                originalSender ! HttpResponse(StatusCodes.InternalServerError, "Error reading updated dataspace metadata from the database")
                                        }

                                    case _ => 
                                        logger error s"$response2"
                                        originalSender ! HttpResponse(response2.status, "Error updating the dataspace")
                                    }
                            }
                        case (errorCode, errorMessage) =>
                            logger info s"$response1"
                            originalSender ! HttpResponse(errorCode, "Error getting dataspace metadata from CKAN. The dataspace cannot be updated")
                    }
            }
        }

        // Creates a new package in the dataspace
        case CreatePackageInDataspace(authorizationKey, p) => {
            val originalSender = sender

            postRequest("package_create", p, authorizationKey)
                .mapTo[HttpResponse]
                .map { response => originalSender ! response}
        }

        case DeleteDataspace(authorizationKey, id) => {
            val originalSender = sender

            (IO(Http) ? (Post(CkanConfig.namespace + "action/organization_delete", HttpEntity(`application/json`, """{ "id": """"+ id + """"}""" ))~>addHeader("Authorization", authorizationKey)))
            .mapTo[HttpResponse]
            .map { response => response.status match {
                case StatusCodes.OK =>
                    val deletedResource = CkanGodInterface.getDataspace(id)
                    originalSender ! HttpResponse(status = StatusCodes.OK,
                                                  entity = HttpEntity(ContentType(`application/json`, `UTF-8`),
                                                                         deletedResource.map { _.toJson.prettyPrint}.getOrElse {""}))

                case StatusCodes.Forbidden => originalSender ! HttpResponse(response.status, "The supplied authentication is not authorized to access this resource")
                case _ => originalSender ! HttpResponse(response.status, s"""Error deleting dataspace "$id"!""")}
            }
        }

        case response: HttpResponse =>
            println(s"Sending the response back to the requester $response")

        case other =>
            println(s"Found an unknown thing: $other")
            sender ! other
    }

}
