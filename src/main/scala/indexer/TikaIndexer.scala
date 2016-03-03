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

import com.hp.hpl.jena.rdf.model.{ModelFactory, Model, Resource, Property}
import nepomuk.ontology.NIE

import javelin.ontology.Implicits._
import com.hp.hpl.jena.vocabulary.{RDF, DC_11}
import virtuoso.jena.driver.VirtGraph
import collection.mutable.{ MultiMap => MutableMultiMap, HashMap => MutableHashMap, Set => MutableSet }
import collection.immutable.{ HashMap, Map }
import com.hp.hpl.jena.vocabulary.RDF.{`type` => a}

import org.apache.tika.metadata.{ Metadata => TikaMetadata, Property => TikaProperty }
import org.apache.tika.metadata.TikaCoreProperties
import fr.inria.aviz.tikaextensions.tika.CendariProperties
import common.Config.{ Virtuoso => VirtuosoConfig }

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
            // "resourceName"                  -> "uri",    /*TikaMetadata.RESOURCE_NAME_KEY*/
            // "Content-Type"                  -> "format", /*TikaMetadata.CONTENT_TYPE*/
            "text"                          -> "text",

            "Application-Name"              -> "application",
            "Creation-date"                 -> "date",
            "cendari:nerd"                  -> "cleaner_text",
            "cendari:coverage"              -> "coverage",
            "cendari:identifier"            -> "identifier",
            "cendari:provider"              -> "dataProvider",
            "cendari:relation"              -> "relation",
            "cendari:rights"                -> "rights",
            "cendari:source"                -> "source",
            "cendari:type"                  -> "resourceType"
            )

        val pfields = Map[TikaProperty, String](
            // TikaCoreProperties.CREATED      -> "date",
            // TikaCoreProperties.CREATOR_TOOL -> "application",
            // TikaCoreProperties.MODIFIED     -> "date",

            CendariProperties.DATE          -> "date",
            CendariProperties.TITLE         -> "title",
            CendariProperties.CREATOR       -> "creatorName",
            CendariProperties.KEYWORDS      -> "tag",
            CendariProperties.PUBLISHER     -> "publisher",
            CendariProperties.CONTRIBUTOR   -> "contributorName",

            CendariProperties.DESCRIPTION   -> "description",
            CendariProperties.PERSON        -> "personName",
            CendariProperties.ORGANIZATION  -> "org",
            // CendariProperties.TAG           -> "tag",
            CendariProperties.REFERENCE     -> "ref",
            CendariProperties.EVENT         -> "event",
            CendariProperties.PLACE         -> "place",
            CendariProperties.FORMAT        -> "format",

            CendariProperties.LANG          -> "language"
            // TikaCoreProperties.LANGUAGE     -> "language"
            )

        // Geting values for string keys
        sfields.foreach {
            m =>
                val values = metadata.getValues(m._1)
                if (values != null)
                    values.foreach { result.addBinding(m._2, _) }
        }

        // Getting values for property keys
        pfields.foreach {
            m =>
                val values = metadata.getValues(m._1)
                if (values != null)
                    values.foreach { result.addBinding(m._2, _) }
        }

        // Getting all our special keys - anything that starts with cendari:
        metadata.names()
            .filter(_.startsWith("cendari:"))
            .map(_.replace("cendari:", "cendari_"))
            .foreach { key =>
                val values = metadata.getValues(key)
                if (values != null)
                    values.foreach {
                        result.addBinding(key, _)
                    }
            }

        // Getting location properties
        if (metadata.get(TikaCoreProperties.LATITUDE) != null && metadata.get(TikaCoreProperties.LONGITUDE) != null) {
            val latlon =
                    metadata.get(TikaCoreProperties.LATITUDE) +
                    ", " +
                    metadata.get(TikaCoreProperties.LONGITUDE)
            result.addBinding("place", latlon)
        }

        result
    }

    def dc_terms(what: String) = "http://purl.org/dc/terms/" #> what
    def schema(what: String) = "http://schema.org/" #> what
    def foaf(what: String)   = "foaf:" #> what
    def edm(what: String)    = "http://www.europeana.eu/schemas/edm/" #> what
    def skos(what: String)   = "http://www.w3.org/2004/02/skos/core#" #> what

    def addOptionalProperties[T](root: Resource, property: Property, values: Iterable[T]) =
        if (values != null) values.foreach { value => root += (property % value) }

    def runTika(resource: ckan.Resource, file: java.io.File, root: => Resource): Option[Double] = try {

        val metadata = tikaIndexer.parseDocument(file.getName, null, new java.io.FileInputStream(file), -1)
        val info = parseMetadata(metadata) //elasticIndexer convertMetadata metadata

        // Looking for the dataspace
        val dataspace = ckan.DataspaceResourceTable.dataspaceForResource(resource.id)

        dataspace.map { dataspace =>
            info.addBinding("project", dataspace.title getOrElse dataspace.name)
            info.addBinding("groups_allowed", dataspace.name)
            info.addBinding("dataspaceId", dataspace.id)
        }

        info.addBinding("uri", resource.webstoreUrl getOrElse resource.viewDataUrl)
        info.addBinding("url", resource.webstoreUrl getOrElse resource.viewDataUrl)
        info.addBinding("application", "repository")
        info.addBinding("resourceId",  resource.id)

        val mimetype = resource.localMimetype
        info.addBinding("format", mimetype)

        if (mimetype == "application/ead+xml" || mimetype == "application/eag+xml") {
            info.addBinding("application", "archives")
        }

        val text = info.get("cleaner_text").getOrElse(info.get("text").get)

        if (!text.head.isEmpty) {
            AbstractIndexer.saveAttachment(resource, text.head, "text/plain")
            addOptionalProperties(root, NIE.plainTextContent, text)
        }

        AbstractIndexer.saveAttachment(resource,
            (HashMap[String, Set[String]]() ++ info).toJson.prettyPrint,
            "application/x-elasticindexer-json-output")
        AbstractIndexer.saveAttachment(resource,
            metadata.toString,
            "application/x-tika-indexer-output")

        // TODO: We might want to save the plain text in Virtuoso,
        // or other fields

        addOptionalProperties(root, DC_11.title           , metadata.getValues("title"))
        addOptionalProperties(root, skos("prefLabel")     , metadata.getValues("title"))
        addOptionalProperties(root, DC_11.identifier      , metadata.getValues("reference"))
        addOptionalProperties(root, DC_11.date            , metadata.getValues("date"))
        addOptionalProperties(root, DC_11.`type`          , metadata.getValues("type"))
        addOptionalProperties(root, DC_11.description     , metadata.getValues("description"))
        addOptionalProperties(root, DC_11.publisher       , metadata.getValues("publisher"))
        addOptionalProperties(root, DC_11.language        , metadata.getValues("lang"))
        addOptionalProperties(root, dc_terms("references"), metadata.getValues("reference"))

        addOptionalProperties(root, DC_11.creator         , metadata.getValues("creator"))
        addOptionalProperties(root, DC_11.contributor     , metadata.getValues("contributor"))

        addOptionalProperties(root, DC_11.subject         , metadata.getValues("tag"))
        addOptionalProperties(root, DC_11.coverage        , metadata.getValues("coverage"))
        addOptionalProperties(root, DC_11.identifier      , metadata.getValues("identifier"))
        addOptionalProperties(root, DC_11.relation        , metadata.getValues("relation"))
        addOptionalProperties(root, DC_11.rights          , metadata.getValues("rights"))
        addOptionalProperties(root, DC_11.source          , metadata.getValues("source"))

        root ++=
            resourceMention(resource, "organization", foaf("Organization"), metadata)
                .map(schema("mentions") % _)
        root ++=
            resourceMention(resource, "event", edm("Event"), metadata)
                .map(schema("mentions") % _)
        root ++=
            resourceMention(resource, "place", edm("Place"), metadata)
                .map(schema("mentions") % _)
        root ++=
            resourceMention(resource, "person", foaf("Person"), metadata)
                .map(schema("mentions") % _)


        Some(0.85)
    } catch {
        case e: Throwable =>
            // logger info s"Tika failed: ${e}"
            resource writeLog s"TikaIndexer: Error: ${e}"
            None
    }

    def resourceMention(resource: ckan.Resource, key: String, what: Property, metadata: TikaMetadata): List[Resource] = {

        val values = metadata.getValues(s"cendari:${key}")

        if (values != null) {
            // This is evil. And against the system design we had since the beginning.
            // But the users want a smaller database...
            values.flatMap { value =>
                try {
                    val entityResource = s"http://resources.cendari.dariah.eu/${key}s/" + java.net.URLEncoder.encode(value, "utf-8")
                    val entitiesGraph = new VirtGraph("http://resources.cendari.dariah.eu/entitiesGraph", VirtuosoConfig.url, VirtuosoConfig.user, VirtuosoConfig.password)

                    val model = ModelFactory.createModelForGraph(entitiesGraph)

                    model.createResource(entityResource) ++= Seq(
                        a % what,
                        skos("prefLabel") % value
                    )

                    // entitiesGraph add entityResourceOb
                    List(?:(entityResource))
                } catch {
                    case e: Exception =>
                        resource.writeLog(s"resourceMention error ${e.toString}")
                        List()
                }
            }.toList
        } else {
            List()
        }
    }

    override
    def indexAttachment(
        resource: conductor.ResourceAttachment,
        file: java.io.File,
        mimetype: String,
        root: => Resource
    ): Option[Double] = None
}
