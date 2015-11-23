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

package indexer

import com.hp.hpl.jena.rdf.model.{Resource, ModelFactory, Model}
import com.hp.hpl.jena.vocabulary.DC_11
import virtuoso.jena.driver.{VirtGraph, VirtModel}

import javelin.ontology.Implicits._
import spray.json._
import com.hp.hpl.jena.vocabulary.RDF.{`type` => a}
import conductor.ResourceAttachmentUtil._
import common.Config.{ Virtuoso => VirtuosoConfig }

case class NerdItem(itemType: String, score: Double, text: String)
case class NerdDocument(entities: List[NerdItem])

object NerdItemProtocol extends DefaultJsonProtocol {
    // lazy val logger = org.slf4j.LoggerFactory getLogger getClass

    implicit object NerdItemFormat extends RootJsonFormat[NerdItem] {
        def read(value: JsValue) =
            value.asJsObject.getFields("type", "nerd_score", "rawName") match {
                case Seq(JsString(itemType), JsString(_score), JsString(text)) =>
                    try {
                        val score = _score.toDouble
                        new NerdItem(itemType, score, text)
                    } catch {
                        case ex : Exception =>
                            ex.printStackTrace
                            null
                    }

                // case collection.mutable.ArrayBuffer(itemType, _score, text) =>
                //     try {
                //         val score = _score.convertTo[String].toDouble
                //         new NerdItem(itemType.convertTo[String], score, text.convertTo[String])
                //     } catch {
                //         case ex : Exception =>
                //             ex.printStackTrace
                //             null
                //     }

                case elseval =>
                    // logger info elseval.toString
                    null
            }

        def write(c: NerdItem) = throw new SerializationException("Writing to JSON is not supported")
    }
}

import NerdItemProtocol._


final
class NerdJsonIndexer extends AbstractIndexer {

    // lazy val logger = org.slf4j.LoggerFactory getLogger getClass

    override
    def indexFile(
        resource: ckan.Resource,
        file: java.io.File,
        mimetype: String,
        rootResource: => Resource
    ): Option[Double] = None

    def schema(what: String) = "http://schema.org/" #> what

    def resourceMention(resource: conductor.ResourceAttachment, item: NerdItem): List[Resource] = {

        val what = item.itemType match {
                    case "PERSON"       => "Person"
                    case "EVENT"        => "Event"
                    case "LOCATION"     => "Place"
                    case "ORGANISATION" => "Organisation"
                    // case "PERIOD"    => "Period"
                    case _              => null
                }

        if (what != null) {
            // This is evil. And against the system design we had since the beginning.
            // But the users want a smaller database...
            try {
                val entityResource = s"http://resources.cendari.dariah.eu/${what.toLowerCase}s/" + java.net.URLEncoder.encode(item.text, "utf-8")
                val entitiesGraph = new VirtGraph("http://resources.cendari.dariah.eu/entitiesGraph", VirtuosoConfig.url, VirtuosoConfig.user, VirtuosoConfig.password)

                val model = ModelFactory.createModelForGraph(entitiesGraph)

                model.createResource(entityResource) ++= Seq(
                        a % schema(what),
                        schema("name") % item.text
                    )

                // entitiesGraph add entityResourceOb
                List(?: (entityResource))
            } catch {
                case e: Exception =>
                    resource.writeLog(s"resourceMention error ${e.toString}")
                List()
            }

            // val query = s"""|SPARQL INSERT IN GRAPH <http://resources.cendari.dariah.eu/entitiesGraph> {
            //                 |<${entityResource}> a <${schema(what)}> .
            //                 |<${entityResource}> <${schema("name")}> ??
            //                 |}""".stripMargin// ''${escape(item.text)}'^^xsd:string .
            //
            // try {
            //     val statement = conductor.plugins.VirtuosoFeederPlugin.prepare(query)
            //     statement.setString(1, item.text)
            //     statement.execute()
            //     List(?: (entityResource))
            // } catch {
            //     case e: java.sql.SQLException =>
            //         resource.writeLog(s"SPARQL error ${e.getMessage} ${e.toString} \n query was: ${query}")
            //     List()
            // }
        } else {
            List()
        }
    }

    override
    def indexAttachment(
        resource: conductor.ResourceAttachment,
        file: java.io.File,
        mimetype: String,
        rootResource: => Resource
    ): Option[Double] =
        if (mimetype != "application/x-nerd-output") None
        else try {
            resource writeLog s"NerdJsonIndexer: indexing nerd output file: ${file.toURI} ${DC_11.title} ${file.getName}"

            val jsondata = scala.io.Source.fromFile(file).mkString
            val nerdDocument = JsonParser(jsondata).asJsObject

            // logger info nerdDocument.fields("entities").toString

            val nerdItems = nerdDocument.fields("entities").convertTo[List[NerdItem]]

            val root = rootResource

            val mention = (item: NerdItem) => resourceMention(resource, item)

            root ++= nerdItems
                        .filter(_ != null)
                        .filter(_.score > .75)
                        .flatMap(mention)
                        .map(schema("mentions") % _)

            // logger info s"created resource: ${root.getURI} ${root}"

            Some(.95)
        } catch {
            case e: Exception =>
                // logger info "Failed to index the attachment"
                resource writeLog s"Failed to index the attachment ${e.toString}"
                None
        }
}
