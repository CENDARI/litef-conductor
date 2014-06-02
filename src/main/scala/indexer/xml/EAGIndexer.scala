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

package indexer.xml

import scala.xml.{ Elem, Node }

import com.hp.hpl.jena.rdf.model.{ Resource, Property }
import com.hp.hpl.jena.sparql.vocabulary.FOAF
import com.hp.hpl.jena.vocabulary.{ RDFS, XSD }
import com.hp.hpl.jena.vocabulary.RDF.{`type` => a}

import indexer.AbstractIndexer
import javelin.ontology.Javelin
import cendari.ontology.CAO
import w3c.ontology.vCard

import javelin.ontology.Implicits._

class EAGIndexer extends indexer.XMLIndexer {

    override
    val extensions = Seq(".eag", ".eag.xml")

    override
    val labels = Seq("eag")

    override
    def indexFile(root: Resource, xml: Elem): Option[Double] = {
        // Adding the history information to the resource
        // root <= history(xml).map { Javelin.hasHistory % _ }

        // root <= desc(xml).map { CAO.hasRepositoryAddress % _ }

        desc(xml).map { resource =>
            resource += (RDFS.isDefinedBy % root)
        }

        // logger warn "---------------------------------------------------------"
        // model.write(System.out, "N3")
        // logger warn "---------------------------------------------------------"

        Some(0.9)
    }

    def desc(xml: Elem): Iterable[Resource] =
        xml \\ "archguide" \\ "desc" map { node =>
            // Creating a repository instance
            val result = ? (a % CAO.Repository)

            // Adding the contact information
            result ++= node.child.flatMap { child =>
                val predicate: Option[Property] = child.label match {
                    case "telephone" => Some(CAO.hasPhone)
                    case "email"     => Some(vCard.hasEmail)
                    case "webpage"   => Some(vCard.hasURL)

                    case "repositorhist" => Some(CAO.repositoryHistoryNote)


                    case label       => println(s"Unproc label: $label"); None
                }

                predicate.map { _ % child.text }
            }

            // Adding the address
            result ++= address(node).map { CAO.hasRepositoryAddress % _ }

            result

        }

    def address(xml: Node): Iterable[Resource] =
        xml \\ "location" map { node =>
            ? (
                a % vCard.Address,
                vCard.`country-name`   % (node \\ "country").text,
                vCard.`postal-code`    % (node \\ "postalcode").text,
                vCard.`locality`       % (node \\ "municipality").text,
                vCard.`street-address` % (node \\ "street").text
            )

        }

    // def history(xml: Elem): Iterable[Resource] =
    //     xml \\ "mainevent" map { node =>
    //         val modificationTime =
    //             AbstractIndexer.parseDateTime((node \\ "date" \\ "@normal").text, XSD.date.getURI)
    //
    //         logger info s"Got an event at ${modificationTime}"
    //
    //         // Creating a history event, setting the modification time
    //         // and adding the information about the author(s)
    //         ? (
    //             a % Javelin.HistoryEvent,
    //             Javelin.hasModificationTime % modificationTime
    //         ) <= persons(node).map { FOAF.maker % _ }
    //     }
    //
    // def persons(xml: Node): Iterable[Resource] =
    //     xml \\ "person" map { node =>
    //         // Getting the basic info about the author
    //         // so that we could use it for the rdfs:label
    //         // and the foaf:* fields
    //         val name     = (node \\ "forename").text
    //         val surname  = (node \\ "surname").text
    //         val org      = (node \\ "orgName").text
    //
    //         logger info s"Got: ${name} ${surname} (foaf:Person)"
    //         // For persons, we'll be using the following URI format
    //         // litef:/foaf/Person/organization/name+surname
    //         val person = ?: (s"litef:/foaf/Person/${name}+${surname}",
    //             a             % FOAF.Person,
    //             RDFS.label    % s"${name} ${surname}",
    //             FOAF.name     % name,
    //             FOAF.surname  % surname
    //             // ,
    //             // FOAF.member   % (
    //             //                     ?: (s"litef:/foaf/Organization/${orgReal}",
    //             //                         a             % FOAF.Organization,
    //             //                         RDFS.label    % orgReal,
    //             //                         FOAF.name     % orgReal
    //             //                     )
    //             //                 )
    //         )
    //
    //         if (!org.isEmpty) {
    //             // For organizations, we'll be using the following URI format
    //             // litef:/foaf/Organization/name
    //             val organization = ?: (s"litef:/foaf/Organization/${org}",
    //                 a             % FOAF.Organization,
    //                 RDFS.label    % org,
    //                 FOAF.name     % org
    //             )
    //
    //             person <= Seq(FOAF.member % organization)
    //         }
    //
    //         person
    //
    //     }

}
