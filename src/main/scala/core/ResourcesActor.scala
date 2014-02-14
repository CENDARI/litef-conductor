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

import akka.actor.Actor
import java.security.Timestamp

object ResourcesActor {
  case class GetResources(implicit val since: Option[Timestamp], implicit val until: Option[Timestamp])
  case class GetResource(id: String, implicit val since: Option[Timestamp], implicit val until: Option[Timestamp])
}

class ResourcesActor extends Actor{
  import ResourcesActor._

  // notice that we don't actually perform any DB operations.
  // that's for another template
  def receive: Receive = {
    case GetResource(id, since, until) =>
      sender ! "Hi!"
  }

}
