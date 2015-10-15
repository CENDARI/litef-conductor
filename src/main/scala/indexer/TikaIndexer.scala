/*
 * Copyright (C) 2015 Ivan Cukic <ivan at mi.sanu.ac.rs>
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

import com.hp.hpl.jena.rdf.model.{ Model, Resource, Property }
import org.foment.utils.Exceptions._
import nepomuk.ontology.NIE

import javelin.ontology.Implicits._
import com.hp.hpl.jena.vocabulary.DC_11
import collection.mutable.{ MultiMap => MutableMultiMap, HashMap => MutableHashMap, Set => MutableSet }
import collection.immutable.{ HashMap, Map }

import org.apache.tika.metadata.{ Metadata => TikaMetadata, Property => TikaProperty }
import org.apache.tika.metadata.TikaCoreProperties
import fr.inria.aviz.tikaextensions.tika.CendariProperties

import spray.json._
import DefaultJsonProtocol._

final
class TikaIndexer extends AbstractIndexer {
    override
    def indexFile(
        resource: ckan.Resource,
        file: java.io.File,
        mimetype: String,
        root: => Resource
    ): Option[Double] =
        runTika(resource, file, root)

    lazy val tikaIndexer = fr.inria.aviz.tikaextensions.TikaExtensions.instance

    def parseMetadata(metadata: TikaMetadata) = {
        val result = new MutableHashMap[String, MutableSet[String]] with MutableMultiMap[String, String]

        val sfields = Map[String, String](
            "resourceName"                  -> "uri",    /*TikaMetadata.RESOURCE_NAME_KEY*/
            "Content-Type"                  -> "format", /*TikaMetadata.CONTENT_TYPE*/
            "text"                          -> "text",

            "Application-Name"              -> "application",
            "Creation-date"                 -> "date"
            )

        val pfields = Map[TikaProperty, String](
            TikaCoreProperties.CREATED      -> "date",
            CendariProperties.DATE          -> "date",
            TikaCoreProperties.TITLE        -> "title",
            TikaCoreProperties.CREATOR      -> "creatorName",
            TikaCoreProperties.CREATOR_TOOL -> "application",
            TikaCoreProperties.KEYWORDS     -> "tag",
            TikaCoreProperties.PUBLISHER    -> "publisher",
            TikaCoreProperties.MODIFIED     -> "date",
            TikaCoreProperties.CONTRIBUTOR  -> "contributorName",

            TikaCoreProperties.DESCRIPTION  -> "description",
            CendariProperties.PERSON        -> "personName",
            CendariProperties.ORGANIZATION  -> "org",
            CendariProperties.TAG           -> "tag",
            CendariProperties.REFERENCE     -> "ref",
            CendariProperties.EVENT         -> "event",

            CendariProperties.LANG          -> "language",
            TikaCoreProperties.LANGUAGE     -> "language"
            )

        sfields.foreach {
            m =>
                if (metadata.get(m._1) != null)
                    result.addBinding(m._2, metadata get m._1)
        }
        pfields.foreach {
            m =>
                if (metadata.get(m._1) != null)
                    result.addBinding(m._2, metadata get m._1)
        }

        if (metadata.get(TikaCoreProperties.LATITUDE)!= null && metadata.get(TikaCoreProperties.LONGITUDE) != null) {
            val latlon =
                    metadata.get(TikaCoreProperties.LATITUDE) +
                    ", " +
                    metadata.get(TikaCoreProperties.LONGITUDE);
            result.addBinding("place", latlon);
        }

        if (metadata.get(CendariProperties.PLACE) != null) {
            metadata.getValues(CendariProperties.PLACE).foreach {
                name => result.addBinding("placeName", name)
            }
        }


        result
    }


    def addOptionalProperty[T](root: Resource, property: Property, value: T) =
        if (value != null) root += (property % value)

    def runTika(resource: ckan.Resource, file: java.io.File, root: => Resource): Option[Double] = try {

        val metadata = tikaIndexer.parseDocument(file.getName, null, new java.io.FileInputStream(file), -1)
        val info = parseMetadata(metadata) //elasticIndexer convertMetadata metadata

        resource.group.map { info.addBinding("dataspace", _) }

        info.addBinding("resourceId", resource.id)
        info.addBinding("localPath", resource.localPath)

        val text = info.get("text").get
        AbstractIndexer.saveAttachment(resource, text.head, "text/plain")
        AbstractIndexer.saveAttachment(resource,
            (HashMap[String, Set[String]]() ++ info).toJson.prettyPrint,
            "application/x-elasticindexer-json-output")

        // TODO: We might want to save the plain text in Virtuoso,
        // or other fields

        addOptionalProperty(root, NIE.plainTextContent , metadata.get("text"))
        addOptionalProperty(root, DC_11.title          , metadata.get("title"))
        addOptionalProperty(root, DC_11.creator        , metadata.get("creator"))
        addOptionalProperty(root, DC_11.date           , metadata.get("date"))
        addOptionalProperty(root, DC_11.format         , metadata.get("format"))

        Some(0.85)
    } catch {
        case e: Throwable =>
            logger info s"Tika failed: ${e}"
            resource writeLog s"TikaIndexer: Error: ${e}"
            None
    }

    override
    def indexAttachment(
        resource: conductor.ResourceAttachment,
        file: java.io.File,
        mimetype: String,
        root: => Resource
    ): Option[Double] = None
}
