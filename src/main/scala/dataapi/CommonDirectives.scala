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

import scala.concurrent.ExecutionContext
import spray.routing._
import java.sql.Timestamp

/**
 * Convinience directives for creating path extractors
 * @param executionContext
 */
class CommonDirectives(implicit executionContext: ExecutionContext)
    extends Directives
    with DefaultFormats
    with dataapi.DefaultValues
{
    // Dataspace listing can be filtered on the creation time.
    val timeRestriction =
        parameter('since.as[Timestamp]?) &
        parameter('until.as[Timestamp]?)
}
