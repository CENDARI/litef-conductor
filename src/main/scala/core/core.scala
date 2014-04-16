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

import akka.actor._
import akka.actor.DeadLetter

object Core {
    // Creating the system actor
    private implicit val _ = system.dispatcher
    implicit lazy val system = ActorSystem("akka-spray")

    // class Listener extends Actor {
    //     def receive = {
    //         case d: DeadLetter =>
    //             println(s"Found a dead letter: $d")
    //     }
    // }
    // val listener = system.actorOf(Props(classOf[Listener], this))
    // system.eventStream.subscribe(listener, classOf[DeadLetter])

    // Kill the application on jvm exit
    sys addShutdownHook system.shutdown()

    // System actors
    lazy val resourceActor  = system.actorOf(Props[ResourceActor],  "resource-actor")
    lazy val dataspaceActor = system.actorOf(Props[DataspaceActor], "dataspace-actor")
}
