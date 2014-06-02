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

import scala.collection.JavaConversions._

import com.hp.hpl.jena.query.{ QueryExecutionFactory, QueryFactory, QuerySolution }
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.reasoner.ReasonerRegistry
import com.hp.hpl.jena.sdb.SDBFactory

object Store {

    lazy val logger = org.slf4j.LoggerFactory getLogger getClass

    /**
     * The main store
     */
    // lazy val model = ModelFactory.createInfModel(reasoner, storeModel)
    // TODO: We will probably use some inferencer
    lazy val model = storeModel

    /**
     * Prefixes the specified query with the namespaces definitions
     * @param queryString query
     * @returns the resulting query
     */
    private
    def addPrefixesToQuery(queryString: String) =
        prefixes.reduceRight(_ + "\n" + _) + "\n" + queryString

    /**
     * Writes the query to the standard output
     */
    private
    def printQuery(queryString: String) {
        val lines = queryString.split("\n")
        logger info ("Query: " + lines.head)
        lines.tail.foreach { line =>
            logger info ("       " + line)
        }
    }

    /**
     * Queries the database
     * @param queryString SPARQL query
     */
    def executeQuery(queryString: String) : Iterator[QuerySolution] = {
        val prefixedQuery = addPrefixesToQuery(queryString)

        printQuery(prefixedQuery)

        QueryExecutionFactory.create(
            QueryFactory create prefixedQuery,
            model
        ).execSelect
    }

    /**
     * Alias for executeQuery
     * {{{
     *     Store ?* "select distinct ?r a rdfs:Class ." map { _.toString } foreach { println }
     * }}}
     */
    def ?*(queryString: String) = executeQuery(queryString)

    /**
     * Similar to executeQuery, but meant for queries that have only one
     * variable in the select clause that needs to be named ?r
     * @param whereClause where clause of the query
     * Example:
     * {{{
     *     Store ? "?r a rdfs:Class ." map { _.toString } foreach { println }
     * }}}
     */
    def ?(whereClause: String) =
        for (result <- executeQuery(s"select distinct ?r where { ${whereClause} } "))
            yield result.get("r")

    /**
     * Adds a new prefix to the queries
     */
    def addPrefix(prefix: String, uri: String) = {
        prefixes = s"prefix ${prefix}: <${uri}>" :: prefixes
    }

    private
    val store = SDBFactory connectStore "share/config/sdb.ttl"

    private
    val storeModel = SDBFactory connectDefaultModel store

    private
    val reasoner = ReasonerRegistry.getOWLReasoner bindSchema storeModel

    private
    var prefixes = List[String] (
        "prefix rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>",
        "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>",
        "prefix xsd:  <http://www.w3.org/2001/XMLSchema#>",
        "prefix fn:   <http://www.w3.org/2005/xpath-functions#>",
        "prefix foaf: <http://xmlns.com/foaf/0.1/>"
        )

}
