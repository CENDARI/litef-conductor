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

import com.hp.hpl.jena.rdf.model.Resource
import scala.xml._
import org.foment.utils.Exceptions._

abstract
class XMLIndexer extends AbstractIndexer {
    lazy val logger = org.slf4j.LoggerFactory getLogger getClass

    val extensions: Seq[String]
    val labels: Seq[String]

    final override
    def indexFile(
        resource: ckan.Resource,
        file: java.io.File,
        mimetype: String,
        rootResource: => Resource
    ): Option[Double] = exceptionless (
        if (mimetype != "application/xml" &&
            mimetype != "text/xml") {
            logger info s"mimetype is not xml: ${mimetype}"
            None

        } else if (!canIndexFile(file.getName)) {
            // logger info "we can not index this file - the extension is bad"
            None

        } else {
            val xml = XML loadFile file
            if (canIndexXML(xml.label)) {
                indexFile(rootResource, XML loadFile file)
            } else {
                // logger info s"We can not index this xml type ${xml.label}"
                None
            }
        }
        , "Error indexing file: " + file.getCanonicalPath )

    def canIndexFile(filename: String) =
        true // CKAN does not keep extensions...
        // extensions contains { ext: String => filename endsWith ext }

    def canIndexXML(rootLabel: String) =
        labels contains rootLabel

    def indexFile(root: Resource, xml: Elem): Option[Double]
}
