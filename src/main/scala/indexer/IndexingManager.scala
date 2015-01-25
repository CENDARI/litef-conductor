/*
 * Copyright (C) 2013, 2014, 2015 Ivan Cukic <ivan at mi.sanu.ac.rs>
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
import com.hp.hpl.jena.rdf.model.{ Model, ModelFactory, Resource, Literal, ResourceFactory}
import org.foment.utils.Filesystem._
import org.foment.utils.Class._
import VirtuosoInterface._
import conductor._
import ckan.CkanGodInterface.database
import slick.driver.PostgresDriver.simple._
import common.Config


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

    def saveGeneratedModel(resource: ckan.Resource, model: Model,
            format: String, mimetype: String): Option[String] = {

        val stream = new java.io.ByteArrayOutputStream()
        model.write(stream, format)

        val result = AbstractIndexer.saveAttachment(resource, stream.toString, mimetype)

        stream.close
        result
    }

    def index(resource: ckan.Resource) {
        val f = new File("/opt/litef/runtime-block-indexing");
        if (f.exists()) return;

        val ckanFile = resource.localFile
        val mimetype = resource.localMimetype

        val results = indexers flatMap {
            _.index(resource, ckanFile, mimetype)
                .filter(_.score > .75)
                .map(result => Result(ckanFile, result.indexerName, result.model))
        }

        val joinedModel = ModelFactory.createDefaultModel

        // Adding indexing results
        results foreach { result =>
            // println(s"Adding model: ${result.indexerName}")
            joinedModel add result.model
            // result.model.write(System.out, "N3")
            // namedGraph add result.model
        }

        // println("#### [BEGIN]  This is what we have generated: (Joined model) ####")
        // joinedModel.write(System.out, "N3")
        // println("#### [END]    This is what we have generated: (Joined model) ####")

        // We need to save the new RDF serializations back to the database
        val serializedFile = saveGeneratedModel(resource, joinedModel, "RDF/XML", "application/rdf+xml")
        saveGeneratedModel(resource, joinedModel, "N3", "text/n3")

        // Removing previously generated data
        try {
            val namedGraphUri = "litef://resource/" + resource.id;
            val namedGraph = VirtuosoInterface.namedGraph(namedGraphUri)

            // namedGraph.removeAll
            val clearGraph = VirtuosoInterface.execute(s"SPARQL CLEAR GRAPH <$namedGraphUri>")

            // For some reason, this fails - it generates an invalid query
            // which virtuoso does not understand
            // namedGraph add joinedModel

            // We need to try to force-feed virtuoso
            val file: String = serializedFile.get

            // DB.DBA.TTLP_MT is for TTL and friends, while
            // DB.DBA.RDF_LOAD_RDFXML_MT would be for xml/rdf
            // val virtuosoInsertFunction = "DB.DBA.TTLP_MT"
            val virtuosoInsertFunction = "DB.DBA.RDF_LOAD_RDFXML_MT"

            VirtuosoInterface.execute(
                    s"""|CALL
                        |$virtuosoInsertFunction(
                        |    file_to_string_output('$file'),
                        |    '',
                        |    '$namedGraphUri'
                        |)""".stripMargin
                )

            // println("#### [BEGIN]  This is what we have generated: (Virtuoso model) ####")
            // namedGraph.write(System.out, "N3")
            // println("#### [END]    This is what we have generated: (Virtuoso model) ####")

        } catch {
            case e: com.hp.hpl.jena.shared.JenaException =>
                println("Jena Exception while adding the model to Virtuoso:")

                e.getCause match {
                    case e: virtuoso.jdbc4.VirtuosoException =>
                        println("Virtuoso Exception: " + e.getMessage)
                        //e.iterator.

                    case _ => println("Unknown cause")

                }

            case e: virtuoso.jdbc4.VirtuosoException =>
                println(s"Virtuoso Exception while adding the model: ${e.toString()}")
                // e.printStackTrace


            case e: Exception =>
                println(s"Unknown Exception while adding the model to Virtuoso: ${e.toString()}")

        }


    }

    // def printResults(file: String, mimetype: String) {
    //     indexers flatMap { indexer =>
    //         indexer.index("litef://dump/", new java.io.File(file), mimetype)
    //             .filter(_.score > .75)
    //             .map{_.model.write(System.out, "N3")}
    //     }
    //
    // }

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

    case class Result(file: File, indexerName: String, model: Model)
}
