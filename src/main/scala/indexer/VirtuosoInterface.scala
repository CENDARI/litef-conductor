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

package indexer

import java.io.File
import org.foment.utils.Filesystem._
import org.foment.utils.Class._
import virtuoso.jena.driver.{VirtGraph, VirtModel}
import common.Config.{ Virtuoso => VirtuosoConfig }

object VirtuosoInterface {

    lazy val log = org.slf4j.LoggerFactory getLogger getClass

    lazy val defaultGraph = new VirtGraph(VirtuosoConfig.url, VirtuosoConfig.user, VirtuosoConfig.password)

    def namedGraph(name: String) = {
        // logger.info("Creating graph for " + name)
        new VirtGraph(name, VirtuosoConfig.url, VirtuosoConfig.user, VirtuosoConfig.password)
    }

    implicit
    class GraphToModel(graph: VirtGraph) {
        lazy val model = new VirtModel(graph)

    }

}

