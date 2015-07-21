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
import conductor._
import ckan.CkanGodInterface.database
import slick.driver.PostgresDriver.simple._
import common.Config

import conductor.ResourceAttachmentUtil._
import java.sql.Timestamp

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

    // TODO: Refactor attachment methods not to be copies of the Resource ones
    def saveGeneratedModel(
            resourceId: String,
            resourceCreated: Option[Timestamp],
            resourceModified: Option[Timestamp],
            model: Model,
            format: String,
            mimetype: String
            ): Option[String] = {

        val stream = new java.io.ByteArrayOutputStream()
        model.write(stream, format)

        val result = AbstractIndexer.saveAttachment(
            resourceId, resourceCreated, resourceModified, stream.toString, mimetype)

        stream.close
        result
    }

    def saveGeneratedModels(
            resourceId: String,
            resourceCreated: Option[Timestamp],
            resourceModified: Option[Timestamp],
            model: Model,
            mimetypePrefix: String
            ) {

        if (!model.isEmpty) {
            saveGeneratedModel(
                resourceId, resourceCreated, resourceModified,
                model, "RDF/XML", mimetypePrefix + "application/rdf+xml")
            saveGeneratedModel(
                resourceId, resourceCreated, resourceModified,
                model, "N3", mimetypePrefix + "text/n3")
        }

    }

    def index(attachment: conductor.ResourceAttachment) {
        logger.info(s"Indexing attachment: ${attachment.resourceId} ${attachment.format}")
        val attachmentFile = new java.io.File(attachment.localPath)

        val joinedModel = ModelFactory.createDefaultModel

        indexers flatMap {
            _.index(attachment, attachmentFile, attachment.format)
                .filter(_.score > .75)
                .map(result => Result(attachmentFile, result.indexerName, result.model))
        } foreach {
            joinedModel add _.model
        }

        saveGeneratedModels(
            attachment.resourceId, Some(attachment.created), Some(attachment.modified),
            joinedModel, attachment.format + " -> ")
    }

    def index(resource: ckan.Resource) {
        val resourceFile = resource.localFile
        val mimetype = resource.localMimetype

        logger.info(s"Indexing resource:   ${resource.id} ${mimetype}")

        val joinedModel = ModelFactory.createDefaultModel

        indexers flatMap {
            _.index(resource, resourceFile, mimetype)
                .filter(_.score > .75)
                .map(result => Result(resourceFile, result.indexerName, result.model))
        } foreach {
            joinedModel add _.model
        }

        // We need to save the new RDF serializations back to the database
        saveGeneratedModels(
            resource.id, resource.created, resource.modified,
            joinedModel, "")

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

    case class Result(file: File, indexerName: String, model: Model)
}
