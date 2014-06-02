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
object Exceptions {

    var showExceptionBacktrace = false

    lazy val logger = org.slf4j.LoggerFactory getLogger getClass

    /**
     * Prints out the exception, and its back-trace
     * @param message message to show before reporting the exception
     */
    def printException(message: String, e: Exception) = {
        // Writing the exception to the error output
        logger error (message + " " + e)
        if (showExceptionBacktrace) {
            e.printStackTrace(System.err)
        }
    }

    /**
     * Defines a code block that swallows exceptions.
     * It returns the either the value returned by the
     * contained code, or Option.None if an exception
     * was thrown.
     * It should be used mostly for wrapping Java calls
     * or Scala code that is not Option-based to achieve
     * a cleaner design.
     * For example:
     * {{{
     *     optional {
     *         connect to the web server
     *         get a page
     *         return the content
     *     }
     * }}}
     */
    def optional[T](method: => T, errMessage: String = ""): Option[T] =
        try Some(method) catch {
            case e: Exception =>
                printException(errMessage + " Swallowed exception:", e)

                None
        }

    /**
     * Defines a code block that swallows exceptions.
     * Returns null if an exception was thrown.
     */
    def exceptionless[T](method: => Option[T], errMessage: String = ""): Option[T] =
        try method catch {
            case e: Exception =>
                printException(errMessage + "Swallowed exception", e)

                None
        }

    /**
     * Automatically close the resource on exit
     */
    def using[A <: {def close() : Unit}, B](param: A)(function: A => B) : B =
    try {
        function(param)
    } finally {
        param.close
    }

}
