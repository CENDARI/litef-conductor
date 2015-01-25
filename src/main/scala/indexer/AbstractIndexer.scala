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
import common.Config
import ckan.CkanGodInterface.database
import slick.driver.PostgresDriver.simple._

trait AbstractIndexer {
    /**
     * Class that represents the result of performed indexing
     * @param score the score of the result, between 0 and 1
     * @param model triples to be inserted into the database
     */
    case class Result(
        var indexerName: String = "",
        var score: Double = 0.0,
        var model: Model = null)

    /**
     * Starts the indexing of a file
     * @return the indexing results
     * @param file file to parse/index
     * @param mimetype the mime type of the file
     */
    final
    def index(
        resource: ckan.Resource,
        file: java.io.File,
        mimetype: String
    ): Option[Result] = {

        // Clearing the model to receive new data
        model = ModelFactory.createDefaultModel

        // Indexing the file
        val resourceUri = "litef://resource/" + resource.id
        val result = indexFile(resource, file, mimetype, /*lazy*/ ?:(resourceUri))

        if (result.isEmpty) None
        else {
            Some(Result(this.getClass.getName, result.get, model))
        }

    }

    /**
     * Starts the indexing of a file
     * @return the indexing results
     * @param file file to parse/index
     * @param mimetype the mime type of the file
     */
    final
    def index(
        attachment: conductor.ResourceAttachment,
        file: java.io.File,
        mimetype: String
    ): Option[Result] = {

        // Clearing the model to receive new data
        model = ModelFactory.createDefaultModel

        // Indexing the file
        val resourceUri = "litef://resource/" + attachment.resourceId
        val result = indexAttachment(attachment, file, mimetype, /*lazy*/ ?:(resourceUri))

        if (result.isEmpty) None
        else {
            Some(Result(this.getClass.getName, result.get, model))
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
        resource: ckan.Resource,
        file: java.io.File,
        mimetype: String,
        rootResource: => Resource
    ): Option[Double]

    /**
     * Reimplement this method in the subclasses.
     * @return the score of the result
     * @param file file to parse/index
     * @param mimetype the mime type of the file
     * @param rootResource resource to add the data to
     */
    protected
    def indexAttachment(
        resource: conductor.ResourceAttachment,
        file: java.io.File,
        mimetype: String,
        rootResource: => Resource
    ): Option[Double] = None

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

    /**
     * Gets an XML element text
     */
    def nodeText(node: scala.xml.Node): String =
        scala.xml.Utility.escape(node.text)
    def nodeText(nodes: scala.xml.NodeSeq): String =
        scala.xml.Utility.escape(nodes.text)
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

    /**
     * Saving processed formats. Returns an option of the file path where the
     * data has been saved
     */
    def saveAttachment(resource: ckan.Resource, content: String,
            mimetype: String): Option[String]
            = database.withSession { implicit session: Session =>

        try {
            // First, we need to create the directory for the data
            val destinationPath = conductor.ResourceAttachmentUtil.attachmentPathForResource(resource.id)

            val now = new java.sql.Timestamp(System.currentTimeMillis())

            val filePath = destinationPath + '/' + conductor.ResourceAttachmentUtil.attachmentNameForMimetype(mimetype)

            val writer = new java.io.BufferedWriter(new java.io.FileWriter(filePath))
            writer write content
            writer.close

            // val stream = new java.io.ByteArrayOutputStream()
            conductor.ResourceAttachmentTable.query += conductor.ResourceAttachment(
                resource.id,
                mimetype,
                resource.created  getOrElse now,
                resource.modified getOrElse now,
                None // Some(stream.toString("UTF-8"))
            )

            Some(filePath)

        } catch {
            case e: org.postgresql.util.PSQLException =>
                println(e.toString())

                None

            case e: Exception =>
                println(s"Unknown exception $e")

                None
        }
    }

}
