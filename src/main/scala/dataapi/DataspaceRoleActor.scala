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

import akka.actor.Actor
import akka.io.IO
import akka.pattern.ask
import spray.can.Http
import spray.util._
import spray.http._
import spray.httpx.RequestBuilding._
import spray.json._
import spray.http.HttpResponse
import core.Core
import spray.httpx.unmarshalling._
import ckan.UserDataspaceRoleJsonProtocol._
import DefaultJsonProtocol._
import CkanJsonProtocol._
import spray.httpx.SprayJsonSupport._
import common.Config.{ Ckan => CkanConfig }
import scala.concurrent.ExecutionContext.Implicits.global
import MediaTypes._
import HttpCharsets._
import HttpMethods._
import HttpHeaders._
import StateFilter._
import ckan.{ CkanGodInterface, UserDataspaceRole }
import slick.driver.PostgresDriver.simple._
//import scala.slick.lifted.{Column, Query}

object DataspaceRoleActor {
    case class ListDataspaceRoles(val authorizationKey: String, val userId: Option[String], val dataspaceId: Option[String], val state: StateFilter)
    case class GetDataspaceRoleById(val id: String)
    case class CreateDataspaceRole(val authorizationKey: String, val dataspaceRole: UserDataspaceRole)
    case class DeleteDataspaceRole(val authorizationKey: String, val id: String)
}

class DataspaceRoleActor
    extends Actor
    with DefaultValues {

    import DataspaceRoleActor._
    import context.system

    def receive: Receive = {

        case ListDataspaceRoles(authorizationKey: String, userId: Option[String], dataspaceId: Option[String], state: StateFilter) =>
            CkanGodInterface.database withSession { implicit session: Session =>

                val results = CkanGodInterface.listDataspaceRoles(authorizationKey, userId, dataspaceId, state)

                sender ! HttpResponse(status = StatusCodes.OK,
                                      entity = HttpEntity(ContentType(`application/json`, `UTF-8`), JsObject("data" -> results.toJson).prettyPrint))
            }

        case GetDataspaceRoleById(id: String) =>
            CkanGodInterface.database withSession { implicit session: Session =>

                val dsr = CkanGodInterface.getDataspaceRoleById(id)

                sender ! HttpResponse(status = StatusCodes.OK,
                                      entity = HttpEntity(ContentType(`application/json`, `UTF-8`),dsr map {_.toJson.prettyPrint} getOrElse {""}))
            }

        case CreateDataspaceRole(authorizationKey: String, dataspaceRole: UserDataspaceRole) => {
            val originalSender = sender

            (IO(Http) ? (Post(CkanConfig.namespace + "action/organization_member_create", CkanOrganizationMember(dataspaceRole.dataspaceId, dataspaceRole.userId, dataspaceRole.dataspaceRole))~>addHeader("Authorization", authorizationKey)))
            .mapTo[HttpResponse]
            .map { response => response.status match {
                    case StatusCodes.OK =>
                        // NOTE: CKAN API returns null as a result. This has to be read from DB
                        // TODO: CKAN does not accept explicit id for new member in the API call.
                        // How to ensure the returned member is the one created
                        val createdRole = CkanGodInterface.getDataspaceRoleByUserDataspaceAndRole(dataspaceRole.userId, dataspaceRole.dataspaceId, dataspaceRole.dataspaceRole)
                        val id = createdRole.map {_.id}.getOrElse {""}
                        originalSender ! HttpResponse(status = StatusCodes.Created,
                                                      entity = HttpEntity(ContentType(`application/json`, `UTF-8`),
                                                                          createdRole.map { _.toJson.prettyPrint}.getOrElse {""}),
                                                      headers = List(Location(s"${common.Config.namespace}privileges/$id")))
                    case StatusCodes.InternalServerError|StatusCodes.Conflict  => originalSender ! HttpResponse(response.status, "Error creating privilege! Check if dataspace and user exist.")
                    case StatusCodes.Forbidden => originalSender ! HttpResponse(response.status, "The supplied authentication is not authorized to access this resource")
                    case _ => originalSender ! HttpResponse(response.status, "Error creating privilege!")
                    }
            }
        }

        case DeleteDataspaceRole(authorizationKey: String, id: String) =>
            CkanGodInterface.database withSession { implicit session: Session =>

                val originalSender = sender
                val dataspaceRole = CkanGodInterface.getDataspaceRoleById(id)

                dataspaceRole match {
                    case None => originalSender ! HttpResponse(StatusCodes.NotFound, s"""Error deleting privilege! Privilege with id "$id" does not exist!""")
                    case Some(dsr) =>
                        // TODO: Don't send the role. CKAN API ignores it, only user id and dataspace id are relevant
                        (IO(Http) ? (Post(CkanConfig.namespace + "action/organization_member_delete",
                                          CkanOrganizationMember(dsr.dataspaceId, dsr.userId, dsr.dataspaceRole))
                                     ~>addHeader("Authorization", authorizationKey)))
                        .mapTo[HttpResponse]
                        .map { response => response.status match {
                                case StatusCodes.OK =>
                                    originalSender ! HttpResponse(StatusCodes.NoContent)
                                case StatusCodes.Forbidden => originalSender ! HttpResponse(response.status, "The supplied authentication is not authorized to access this resource")
                                case _ => originalSender ! HttpResponse(response.status, "Error deleting privilege!")}
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
