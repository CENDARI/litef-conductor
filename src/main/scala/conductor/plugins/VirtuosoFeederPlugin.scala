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

    // lazy val logger = org.slf4j.LoggerFactory getLogger getClass

    override
    def process(resource: ckan.Resource): Future[Unit] = Future {

        val resourceGraph = graphForResource(resource.id)
        val dataspaceGraph = graphForDataspace(resource.group.get)

        // If we are processing a resource, we need to empty out
        // all the data from Virtuoso that we used to have
        VirtuosoFeederPlugin.clearGraph("litef://resource/" + resource.id) // old URI for the graph
        VirtuosoFeederPlugin.clearGraph(resourceGraph)

        for (group <- resource.group) {
            VirtuosoFeederPlugin.
            execute(s"""|SPARQL INSERT IN GRAPH <${graphForDataspace("")}> {
                        |<${dataspaceGraph}> a <http://www.w3.org/2004/03/trix/rdfg-1/Graph> .
                        |<${dataspaceGraph}> rdfs:member <${resourceGraph}> .
                        |<${resourceGraph}> cendari:dataspace <${dataspaceGraph}> .
                        |}""".stripMargin)
        }

        resource writeLog s"VirtuosoFeeder -> Mimetype is ${resource.localMimetype}"

        // Loading the file, if it is a RDF
        if (resource.localMimetype == "application/rdf+xml") {
            val sourcePath = resource.localPath
            val destinationPath = conductor.ResourceAttachment(resource.id, "_copy").localPath

            import java.nio.file.Files
            import java.nio.file.Paths

            resource writeLog s"\t -> Copying: ${sourcePath} to ${destinationPath}"
            Files.copy(Paths.get(sourcePath), Paths.get(destinationPath))

            // logger info s"\t -> Loading the file into Virtuoso: ${destinationPath}"
            resource writeLog s"\t -> Loading the file into Virtuoso: ${destinationPath} into graph ${resourceGraph}"
            VirtuosoFeederPlugin.loadFileInfoGraph(destinationPath, resourceGraph)
        }

    }

    override
    def process(attachment: conductor.ResourceAttachment): Future[Unit] = Future {

        if (attachment.format endsWith "application/rdf+xml") {
            val resourceGraph = graphForResource(attachment.resourceId)
            // logger info s"\t -> Loading the file into Virtuoso: ${attachment.localPath}"
            attachment writeLog s"\t -> Loading the file into Virtuoso: ${attachment.localPath} into graph ${resourceGraph}"
            VirtuosoFeederPlugin.loadFileInfoGraph(attachment.localPath, resourceGraph)

        }

    }
}

object VirtuosoFeederPlugin {
    lazy val connection = {
        Class.forName("virtuoso.jdbc4.Driver");
        java.sql.DriverManager.getConnection(
                                s"${VirtuosoConfig.url}/DATABASE=DB/UID=${VirtuosoConfig.user}/PwD=${VirtuosoConfig.password}")
    }

    def execute(query: String) =
        connection.createStatement execute query

    def prepare(query: String) =
        connection.prepareStatement(query)

    def clearGraph(namedGraphUri: String) =
        execute(s"SPARQL CLEAR GRAPH <$namedGraphUri>")

    def loadFileInfoGraph(file: String, namedGraphUri: String) = {
        try {
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
        } catch {
            case e: Exception =>
                e.printStackTrace
        }
    }
}

