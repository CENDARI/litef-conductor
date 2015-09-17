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

package indexer.xml

import scala.xml.{ Elem, Node }

import com.hp.hpl.jena.rdf.model.{ Resource, Property }
import com.hp.hpl.jena.sparql.vocabulary.FOAF
import com.hp.hpl.jena.vocabulary.{ RDFS, XSD }
import com.hp.hpl.jena.vocabulary.RDF.{`type` => a}

import indexer.AbstractIndexer
import cendari.ontology.CAO
import w3c.ontology.vCard

import javelin.ontology.Implicits._
import org.foment.utils.Exceptions._

class EADIndexer extends indexer.XMLIndexer {

    def schema(what: String) = "http://schema.org/" #> what

    def cao(what: String) = "http://cendari.mi.sanu.ac.rs/ontologies/cao#" #> what
    def caoType(what: String) = "http://cendari.mi.sanu.ac.rs/ontologies/cao#" ## what

    // TODO: move to CAO.ttl
    lazy val `CAO.Archive`                = caoType("Archive")
    lazy val `CAO.Class`                  = caoType("Class")
    lazy val `CAO.Collection`             = caoType("Collection")
    lazy val `CAO.Subcollection`          = caoType("Subcollection")
    lazy val `CAO.File`                   = caoType("File")
    lazy val `CAO.Fonds`                  = caoType("Fonds")
    lazy val `CAO.Item`                   = caoType("Item")
    lazy val `CAO.Series`                 = caoType("Series")
    lazy val `CAO.DescriptiveId`          = caoType("DescriptiveId")

    lazy val `CAO.hasDescriptiveId`       = cao("hasDescriptiveId")
    lazy val `CAO.hasScope`               = cao("hasScope")
    lazy val `CAO.hasRelatedMaterial`     = cao("hasRelatedMaterial")
    lazy val `CAO.hasPhysicalDescription` = cao("hasPhysicalDescription")
    lazy val `CAO.hasUnitId`              = cao("hasUnitId")
    lazy val `CAO.hasUnitTitle`           = cao("hasUnitTitle")
    lazy val `CAO.hasUnitDate`            = cao("hasUnitDate")

    override
    val extensions = Seq(".ead", ".ead.xml")

    override
    val labels = Seq("ead")

    override
    val indexerName = "EADIndexer"

    override
    def indexFile(root: Resource, xml: Elem): Option[Double] = exceptionless {
        // Adding the history information to the resource
        // root <= history(xml).map { Javelin.hasHistory % _ }

        // root <= desc(xml).map { CAO.hasRepositoryAddress % _ }

        logger info "EAD indexer processing a new file..."

        desc(xml).map { resource =>
            resource += (RDFS.isDefinedBy % root)
        }

        // logger warn "---------------------------------------------------------"
        // model.write(System.out, "N3")
        // logger warn "---------------------------------------------------------"

        Some(0.9)
    }

    def desc(xml: Elem): Iterable[Resource] =
        xml \\ "archdesc" map { node =>
            // Creating an archive instance
            val result = ? (a % `CAO.Archive`)

            var unprocessed = ""

            // Parsing the descriptive id, it needs to be a direct descendant
            result ++= parseDescriptiveId((node \ "did").head) map { `CAO.hasDescriptiveId` % _ }

            // Parsing children
            parseChildren(result, (node \ "dsc").head)

            result

        }

    lazy val cTagMatcher = """c[0-9][0-9]?""".r

    // Attachments can not (?) be EAD files
    // def indexAttachment

    def parseChildren(resource: Resource, node: Node) = {
            // Parsing children
            (node \\ "dsc").head.child.foreach { child =>
                val childResource =
                    child.label match {
                        case "c"             => parseCollectionTag(child) // just in case
                        case cTagMatcher(_*) => parseCollectionTag(child)
                    }

                resource += (RDFS.member % childResource)
            }
    }

    def parseDescriptiveId(node: Node): Option[Resource] =
        exceptionless {
            Some(? (
                a % `CAO.DescriptiveId`,
                `CAO.hasUnitId`    % nodeText(node \\ "unitid"),
                `CAO.hasUnitTitle` % nodeText(node \\ "unittile"),
                `CAO.hasUnitDate`  % nodeText(node \\ "unitdate"),
                CAO.hasLanguage    % nodeText(node \\ "language"),
                schema("address")  % nodeText(node \\ "addressline")
            ))
        }

    // def address(xml: Node): Iterable[Resource] =
    //     xml \\ "location" map { node =>
    //         ? (
    //             a % vCard.Address,
    //             vCard.`country-name`   % nodeText(node \\ "country"),
    //             vCard.`postal-code`    % nodeText(node \\ "postalcode"),
    //             vCard.`locality`       % nodeText(node \\ "municipality"),
    //             vCard.`street-address` % nodeText(node \\ "street")
    //         )
    //
    //     }

    def parseCollectionTag(node: Node): Resource = {
            // Creating a repository instance
            val resultType = node.attribute("level").getOrElse("item") match {
                    case "collection"    => `CAO.Collection`
                    case "subcollection" => `CAO.Subcollection`
                    case "class"         => `CAO.Class`
                    case "file"          => `CAO.File`
                    case "fonds"         => `CAO.Fonds`
                    case "item"          => `CAO.Item`
                    case "series"        => `CAO.Series`
                    case _               => `CAO.Item`
                }

            val result: Resource = ? (a % resultType)

            // Parsing the descriptive id, it needs to be a direct descendant
            result ++= parseDescriptiveId((node \ "did").head) map { `CAO.hasDescriptiveId` % _ }

            // Parsing scopes
            result ++= (node \ "scopecontent").map{ node =>
                `CAO.hasScope` % node.child.map(_.text).mkString
            }

            // Parsing related materials
            result ++= (node \ "relatedmaterial").map{ node =>
                `CAO.hasRelatedMaterial` % node.child.map(_.text).mkString
            }

            var unprocessed = ""

            // Parsing children
            parseChildren(result, node)

            result
        }

}
