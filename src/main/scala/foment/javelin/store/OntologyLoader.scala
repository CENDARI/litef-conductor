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

package org.foment.javelin.store

import java.io.File

import org.apache.commons.io.FilenameUtils

import com.hp.hpl.jena.rdf.model.{ Model, ModelFactory }
import com.hp.hpl.jena.vocabulary.{ RDFS, RDF }

import org.foment.utils.Filesystem._
import org.foment.utils.Exceptions._

import javelin.ontology.Javelin
import javelin.ontology.Implicits._

object OntologyLoader {

    def loadOntology(configFile: File): Option[Ontology] = optional {
        // Creating the model to read the ontology into
        // If everything goes well, we'll pass it on to be merged
        // into the main model
        val model = ModelFactory.createDefaultModel

        // Reading the ontology properties
        val config = new org.foment.utils.Config(configFile)

        val ontology = Ontology(
            config getString "ontology.label",
            config getString "ontology.comment",
            config getString "ontology.namespace",
            config getString "ontology.abbreviation",
            model
        )

        val filename     = config getString "source.filename"
        val format       = config getString "source.format"

        // Reading the ontology
        val ontologyFile = new File((FilenameUtils getFullPath configFile.getAbsolutePath) + filename)
        model.read(ontologyFile.toURI.toString, format)

        // Adding the ontology meta-data to the model
        SugarResource(model.createResource(ontology.namespace + "metadata")) ++= Seq(
            RDF.`type`                       % Javelin.Ontology,
            RDFS.label                       % ontology.label,
            RDFS.comment                     % ontology.comment,
            Javelin.hasNamespace             % ontology.namespace,
            Javelin.hasNamespaceAbbreviation % ontology.abbreviation
        )

        // Returning the ontology
        ontology
    }

    def loadOntologies: Iterable[Ontology] =
        new File("share/ontologies")
            .descendIntoSubfolders
            .filter  { _.getAbsolutePath endsWith ".ontology" }
            .flatMap { loadOntology }

}
