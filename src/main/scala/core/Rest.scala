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

package core

import akka.io.IO
import akka.actor.Props
import dataapi.{RoutedHttpService, ResourceService, DataspaceService, UserService, SessionService, DataspaceRoleService}
import spray.can.Http
import spray.routing._
import spray.http.HttpHeaders.RawHeader

object Rest extends App
            with RouteConcatenation
            with Directives
{
    private implicit val _ = Core.system.dispatcher

    // Defining the routes for the service
    val routes =
        respondWithHeader(RawHeader("Access-Control-Allow-Origin", "*")) {
            pathPrefix("v1") {
                new SessionService().route ~
                new UserService().route ~
                new DataspaceRoleService().route ~
                new ResourceService().route ~
                new DataspaceService().route
            }
        }

    // Creating the service
    val rootService = Core.system.actorOf(Props(new RoutedHttpService(routes)))

    Core.collectorActor

    // Binding the 8080 port to our server
    IO(Http)(Core.system) ! Http.Bind(rootService, "0.0.0.0", port = 42042)
}
