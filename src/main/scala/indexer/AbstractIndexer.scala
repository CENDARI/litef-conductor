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

package indexer

import java.io.File
import java.text.SimpleDateFormat
import java.util.{ Calendar, TimeZone, UUID }

import com.hp.hpl.jena.datatypes.xsd.{ XSDDatatype, XSDDateTime }
import com.hp.hpl.jena.rdf.model.{ Model, ModelFactory, Resource, Literal, ResourceFactory}
import com.hp.hpl.jena.vocabulary.XSD

import javelin.ontology.Implicits._

trait AbstractIndexer {
    /**
     * Class that represents the result of performed indexing
     * @param score the score of the result, between 0 and 1
     * @param model triples to be inserted into the database
     */
    case class Result(var score: Double = 0.0, var model: Model = null)

    /**
     * Starts the indexing of a file
     * @return the indexing results
     * @param file file to parse/index
     * @param mimetype the mime type of the file
     */
    final
    def index(
        resourceUri: String,
        file: java.io.File,
        mimetype: String
    ): Option[Result] = {

        // Clearing the model to receive new data
        model = ModelFactory.createDefaultModel

        // Indexing the file
        val result = indexFile(file, mimetype, /*lazy*/ ?:(resourceUri))

        if (result.isEmpty) None
        else {
            Some(Result(result.get, model))
        }

    }

    /**
     * Reimplement this method in the subclasses.
     * @return the score of the result
     * @param file file to parse/index
     * @param mimetype the mime type of the file
     * @param rootResource resource to add the data to
     */
    protected
    def indexFile(
        file: java.io.File,
        mimetype: String,
        rootResource: => Resource
    ): Option[Double];

    // I don't really like that indexers depend on Jena
    // but it seems you can't have statements without a model
    private
    var model: Model = null

    def createResource(uri: String) =
        model.createResource(uri)

    def createResource(file: File) =
        model.createResource(file.toURI.toString)

    def createResource() =
        model.createResource("litef:/" + UUID.randomUUID.toString)

    def ?:(pvs: PropertyValue[_]*): Resource = createResource() ++= pvs

    def ?:(file: File, pvs: PropertyValue[_]*) = createResource(file) ++= pvs

    def ?:(uri: String, pvs: PropertyValue[_]*): Resource = createResource(uri) ++= pvs

    def ? = model.createResource()

    def ?(pvs: PropertyValue[_]*): Resource = ? ++= pvs

}

object AbstractIndexer {
    private
    lazy val dateParser = new SimpleDateFormat("yyyy-MM-dd")

    /**
     * Converting a stringified date to a rdf Literal
     */
    def parseDateTime(value: String, rtype: String): Literal = {
        val date = dateParser.parse(value)

        if (XSD.date.getURI == rtype) {
            ResourceFactory.createTypedLiteral(
                dateParser.format(date),
                XSDDatatype.XSDdate
            )

        } else {
            val cal = Calendar.getInstance(TimeZone getTimeZone "GMT")
            cal setTime date
            val dateTime = new XSDDateTime(cal)

            ResourceFactory createTypedLiteral dateTime
        }
    }
}
