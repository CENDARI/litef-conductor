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

import scala.tools.reflect.ToolBox
import scala.reflect.runtime._

/**
 * Defines a nice wrapper to create class instances from a string.
 * For example, it can be used like this:
 * {{{
 *     "org.apache.SomeClass".newInstance()
 * }}}
 */
object Class {

    implicit
    class Loader(source: String) {
        def newInstance[T] =
            java.lang.Class.forName(source).newInstance.asInstanceOf[T]

        def compileSource[T](parent: Object) = {
            val compiler =
                universe
                    .runtimeMirror(parent.getClass.getClassLoader)
                    .mkToolBox()

            compiler
                .eval(compiler.parse(source))
                .asInstanceOf[T]
        }
    }

    // private
    // lazy val compiler = universe.runtimeMirror(getClass.getClassLoader).mkToolBox()

}
