/*
 * Copyright (C) 2013 Ivan Cukic <ivan at mi.sanu.ac.rs>
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

package org.foment.utils

import scala.language.reflectiveCalls

/**
 * Defines some sugar-syntax for managing code that throws exceptions
 */
object Timing {

    lazy val logger = org.slf4j.LoggerFactory getLogger getClass

    /**
     * Measures the speed of execution of the passed block.
     * It returns a pair - the calculation result along with the
     * time it took in miliseconds.
     * {{{
     *     (result, time) = benchmark {
     *         do some calculations
     *     }
     * }}}
     */
    def benchmark[T](method: => T): (T, Long) = {
        val startTime = System.currentTimeMillis
        val result = method
        val endTime = System.currentTimeMillis

        (result, endTime - startTime)
    }

}
