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

package org.foment.javelin.scripts

import java.io.File
import scala.collection.JavaConversions._
import scala.collection.mutable.Buffer

import com.hp.hpl.jena.rdf.model.{ Property, Statement }
import com.hp.hpl.jena.vocabulary.{ RDF, RDFS }

import org.foment.utils.Exceptions._
import org.foment.javelin.store.OntologyLoader

object OntologyToScala {

    lazy val logger = org.slf4j.LoggerFactory getLogger getClass

    def compile(configFile: String, outFile: String): Unit =
        compile(new File(configFile), new File(outFile))

    def compile(configFile: File, outFile: File): Unit =
        OntologyLoader.loadOntology(configFile).foreach { ontology =>
            // Reading the ontology properties
            val config = new org.foment.utils.Config(configFile)

            val ontologyLabel   = config getString "ontology.label"
            val ontologyComment = config getString "ontology.comment"

            val packageName     = config getString "code.package"
            val objectName      = config getString "code.object"

            val namespace       = config getString "ontology.namespace"

            // Getting the ontology statements and converting them
            // to scala code
            val content = ontology.model.listStatements.toList
                .groupBy { _.getSubject.getURI }
                .map     {
                    (statement) =>
                    (   // returning the subject
                        statement._1 substring namespace.length,

                        // label and comment
                        getValue(statement._2, RDFS.label),
                        getValue(statement._2, RDFS.comment),

                        // prettified type
                        getValue(statement._2, RDF.`type`) match {
                            case s if s == RDF.Property.getURI.toString => "property"
                            case _ => "class"
                        }
                    )
                }
                .filter  { _._2 != null }
                .map     { item =>
                   s"""|    /**
                       |     * ${item._2} (${item._4})
                       |     * ${item._3}
                       |     */
                       |    lazy val ${item._1} = NS ${if (item._4 != "property") "##" else "#>"} "${item._1}"
                       |
                       |""".stripMargin
                }
                .mkString

            // Generating the final output
            val out = template
                .replace("PACKAGE_NAME",     packageName)
                .replace("OBJECT_NAME",      objectName)
                .replace("NAMESPACE",        namespace)
                .replace("CONTENT",          content)
                .replace("ONTOLOGY_LABEL",   ontologyLabel)
                .replace("ONTOLOGY_COMMENT", ontologyComment)

            using (new java.io.PrintWriter(outFile)) { _ write out }

            logger info "File successfully written"
        }

    val template =
        """|/*
           | * This file has been automatically generated based on the
           | * OBJECT_NAME ontology.
           | */
           |
           |package PACKAGE_NAME
           |
           |import org.foment.javelin.vocabulary.Utils._
           |
           | /**
           |  * ONTOLOGY_LABEL
           |  * ONTOLOGY_COMMENT
           |  */
           |object OBJECT_NAME {
           |    lazy val NS = "NAMESPACE"
           |
           |CONTENT
           |}
           |""".stripMargin

    private
    def getValue(statements: Buffer[Statement], property: Property): String =
        statements
            .filter     { _.getPredicate == property }
            .map        { _.getObject.toString }
            .headOption
            .getOrElse  { null }

}
