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

import Exceptions._

import java.io.File
import org.apache.commons.io.FilenameUtils

/**
 * Defines some methods for working with files and directories
 */
object Filesystem {

    /**
     * Utility class to return all the files (not directories) that belong
     * to a specified directory.
     * Do not use it directly -- the implicit methods below allows
     * you to do things like:
     * {{{
     *     "/some/path/".descendIntoSubfolders
     * }}}
     */
    implicit
    class PathTraverser(path: File) {

        /**
         * @return an iterator to the list of files that belong to
         *     the specified directory. Empty iterator if the specified
         *     path is not a directory
         */
        private
        def children =
            new Iterable[File] {
                override def iterator =
                    if (path.isDirectory)
                        path.listFiles.iterator
                    else
                        Iterator.empty
            }

        /**
         * @return an iterator to all descendants of the specified
         *     directory (all files that belong to it, or its
         *     subfolders)
         */
        def descendIntoSubfolders: Iterable[File] =
            if (path.isDirectory)
                children
                    .filter  { !_.isHidden }
                    .flatMap { new PathTraverser(_).descendIntoSubfolders }

            else Seq(path)
    }

    /**
     * Utility class that provides a nice API to get the mimetype of
     * a file -- just use:
     * {{{
     *     file.mimetype()
     * }}}
     */
    implicit
    class FileMimetype(path: File) {
        def mimetype = java.nio.file.Files.probeContentType(path.toPath)
    }

    /**
     * Utility class that reads the whole file and returns its contents
     * {{{
     *    file.readContents()
     * }}}
     */
    implicit
    class FileContents(file: File) {
        def readContents = using(io.Source fromFile file) { _.mkString }
    }

    /**
     * Utility class to get the extension of a file
     * {{{
     *    file.getExtension()
     * }}}
     */
    implicit
    class FileExtension(file: File) {
        def getExtension = FilenameUtils getExtension file.getAbsolutePath
    }

}
