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
import ckan.UserJsonProtocol._
import DefaultJsonProtocol._
import CkanJsonProtocol._
import spray.httpx.SprayJsonSupport._
import common.Config.{ Ckan => CkanConfig }
import scala.concurrent.ExecutionContext.Implicits.global
import MediaTypes._
import HttpCharsets._
import HttpMethods._
import HttpHeaders._
import ckan.CkanGodInterface
import slick.driver.PostgresDriver.simple._
import scala.slick.lifted.{Column, Query}
import java.util.UUID

object UserActor {
    case class GetSessionKeyForUser(val user: ShibData)
    case class GetUserById(val id: String)
    case class ListUsers()
}

class UserActor
    extends Actor
    with DefaultValues {

    import UserActor._
    import context.system

    def receive: Receive = {

        case GetSessionKeyForUser(user: ShibData) => {
                CkanGodInterface.database withSession { implicit session: Session =>
                    val originalSender = sender

                    // TODO: Handle users with status = 'deleted'
                    val ckanUser = CkanGodInterface.getUserByOpenId(user.eppn)
                    ckanUser match {
                        // TODO: Update user data (email, full name) if not equal with what is provided in ShibData
                        case Some(cu) =>
                            val ak = cu.apikey.getOrElse("")
                            originalSender ! HttpResponse(StatusCodes.OK,
                                                          HttpEntity(ContentType(`application/json`, `UTF-8`), s"""{"sessionKey": "$ak"}"""))
                        case None =>
                            val id = UUID.randomUUID().toString
                            // TODO: Ensure global uniqueness
                            val name = user.eppn.split("@")(0).toLowerCase()
                            val password = UUID.randomUUID().toString
                            val email = user.mail
                            val fullname = user.cn
                            val openid = user.eppn

                            (IO(Http) ? (Post(CkanConfig.namespace + "action/user_create",
                                              CkanUser(name, email, password, id, fullname, openid))
                                         ~>addHeader("Authorization", CkanConfig.apiKey)))
                            .mapTo[HttpResponse]
                            .map { response => response.status match {
                                    case StatusCodes.OK =>
                                        val createdUser = CkanGodInterface.getUserById(id)
                                        val apikey = createdUser.map {_.apikey.getOrElse("")}.getOrElse {""}
                                        originalSender ! HttpResponse(StatusCodes.OK,
                                                                      HttpEntity(ContentType(`application/json`, `UTF-8`), s"""{"sessionKey": "$apikey"}"""))
                                    case _ => originalSender ! HttpResponse(response.status, "Error getting session key!")}
                            }
                    }
                }
        }

        case GetUserById(id: String) => {
            CkanGodInterface.database withSession { implicit session: Session =>

                val user = CkanGodInterface.getUserById(id)

                sender ! HttpResponse(
                    status = StatusCodes.OK,
                    entity = HttpEntity(
                        ContentType(`application/json`, `UTF-8`),
                        user map {_.toJson.prettyPrint} getOrElse {""}
                    )
                )
            }
        }

        case ListUsers() =>
            CkanGodInterface.database withSession { implicit session: Session =>

                val results = CkanGodInterface.listUsers()

                sender ! (
                    JsObject("data" -> results.toJson).prettyPrint
                )
            }

        case response: HttpResponse =>
            println(s"Sending the response back to the requester $response")

        case other =>
            println(s"Found an unknown thing: $other")
            sender ! other
    }
}
