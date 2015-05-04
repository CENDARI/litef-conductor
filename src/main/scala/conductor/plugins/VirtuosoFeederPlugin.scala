/*
 * Copyright (C) 2014 Ivan Cukic <ivan at mi.sanu.ac.rs>
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

package conductor.plugins

import conductor.AbstractPluginActor
import concurrent.Future
import concurrent.ExecutionContext.Implicits.global

import java.io.File
import org.foment.utils.Filesystem._
import org.foment.utils.Class._
import virtuoso.jena.driver.{VirtGraph, VirtModel}
import common.Config.{ Virtuoso => VirtuosoConfig }

import conductor.ResourceAttachmentUtil._
import indexer.LitefNaming._


class VirtuosoFeederPlugin extends AbstractPluginActor("VirtuosoFeeder")
{
    import context.system

    lazy val logger = org.slf4j.LoggerFactory getLogger getClass

    override
    def process(resource: ckan.Resource): Future[Unit] = Future {

        val resourceGraph = graphForResource(resource.id)

        // If we are processing a resource, we need to empty out
        // all the data from Virtuoso that we used to have
        clearGraph(resourceGraph)

        // Loading the file, if it is a RDF
        if (resource.localMimetype == "application/rdf+xml") {
            logger info s"Loading the file into Virtuoso: ${resource.localPath}"
            loadFileInfoGraph(resource.localPath, resourceGraph)
        }

    }

    override
    def process(attachment: conductor.ResourceAttachment): Future[Unit] = Future {

        if (attachment.format endsWith "application/rdf+xml") {
            val resourceGraph = graphForResource(attachment.resourceId)
            logger info s"Loading the file into Virtuoso: ${attachment.localPath}"
            loadFileInfoGraph(attachment.localPath, resourceGraph)

        }

    }


    lazy val connection = {
        Class.forName("virtuoso.jdbc4.Driver");
        java.sql.DriverManager.getConnection(
                                s"${VirtuosoConfig.url}/DATABASE=DB/UID=${VirtuosoConfig.user}/PwD=${VirtuosoConfig.password}")
    }

    def execute(query: String) =
        connection.createStatement execute query

    def clearGraph(namedGraphUri: String) =
        execute(s"SPARQL CLEAR GRAPH <$namedGraphUri>")

    def loadFileInfoGraph(file: String, namedGraphUri: String) = {
        // DB.DBA.TTLP_MT is for TTL and friends, while
        // DB.DBA.RDF_LOAD_RDFXML_MT would be for xml/rdf
        // val virtuosoInsertFunction = "DB.DBA.TTLP_MT"
        val virtuosoInsertFunction = "DB.DBA.RDF_LOAD_RDFXML_MT"

        execute(
            s"""|CALL
            |$virtuosoInsertFunction(
                |    file_to_string_output('$file'),
                |    '',
                |    '$namedGraphUri'
                |)""".stripMargin
            )

    }
}

