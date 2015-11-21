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

//import spray.routing._
import spray.http._
import spray.json._
import DefaultJsonProtocol._
import java.net.InetAddress
import MediaTypes._
import HttpCharsets._
import spray.httpx.SprayJsonSupport._
import spray.routing.Directives

class StatusService() extends Directives {

  val route =
        path("status") {
            get {
                complete {
                  HttpResponse(status = StatusCodes.OK,
                               entity = HttpEntity(ContentType(`application/json`, `UTF-8`),
                                                   JsObject(
                                                       "status"   -> JsString("OK"),
                                                       "hostname" -> JsString(InetAddress.getLocalHost().getCanonicalHostName()),
                                                       "version"  -> JsString(info.BuildInfo.version),
                                                       "revision" -> JsString(info.BuildInfo.gitHash)
                                                   )
                                                   .prettyPrint))
                }
            }
        }
}
