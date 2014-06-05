/*
 * Copyright (C) 2013, 2014 Ivan Cukic <ivan at mi.sanu.ac.rs>
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
import com.hp.hpl.jena.rdf.model.Model
import org.foment.utils.Filesystem._
import org.foment.utils.Class._
import VirtuosoInterface._

object IndexingManager {
    lazy val logger = org.slf4j.LoggerFactory getLogger getClass

    /**
     * The list of indexers
     */
    private
    lazy val indexers: Iterable[AbstractIndexer] =
        (indexer.config.Indexer.builtin).map {
            pluginName =>
                logger info s"Loading class: ${pluginName}"
                pluginName.newInstance[AbstractIndexer]
        } ++ runtimeIndexers

    /**
     * The score treshold for the result inclusion into the main store
     */
    val scoreTreshold = 0.9

    def index(resource: ckan.Resource) {
        val resourceUri = "litef://resource/" + resource.id
        val file = resource.localFile
        val mimetype = resource.localMimetype
        val namedModel = VirtuosoInterface.namedGraph(
                 "litef://resource/" + resource.id
            ).model

        val results = indexers flatMap { indexer =>
            indexer.index(resourceUri, file, mimetype)
                .filter(_.score > .75)
                .map(result => Result(file, result.model))
        }

        // Removing previously generated data
        namedModel.removeAll

        results foreach { namedModel add _.model }

    }

    def printResults(file: String, mimetype: String) {
        indexers flatMap { indexer =>
            indexer.index("litef://dump/", new java.io.File(file), mimetype)
                .filter(_.score > .75)
                .map{_.model.write(System.out, "N3")}
        }

    }

//  /**
//   * Executes all indexers on the given file or directory
//   * @param root file or directory to index
//   * @return list of models that the indexers created
//   */
//  def index(root: File): Iterable[Result] =
//      root.descendIntoSubfolders.flatMap {
//          path =>
//              val mime = path.mimetype

//              indexers flatMap {
//                  indexer =>
//                      indexer.index(path, mime)
//                          .filter { _.score > .75 }
//                          .map    { result => Result(path, result.model) }
//              }
//      }

//  /**
//   * Executes all indexers on the given file or directory
//   * @param root file or directory to index
//   * @return list of models that the indexers created
//   */
//  def index(root: String): Iterable[Result] = index(new File(root))

    /**
     * @return the list of external indexers to be dynamically loaded
     */
    private
    def runtimeIndexers: Iterable[AbstractIndexer] = {
        val nativeScalaIndexers =
            new File("share/plugins/indexers/native").descendIntoSubfolders
                .filter { _.getAbsolutePath.endsWith(".scala") }
                .map { _.readContents.compileSource[AbstractIndexer](this) }

        nativeScalaIndexers
    }

    case class Result(file: File, model: Model)
}
