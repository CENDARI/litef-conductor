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

package indexer

import com.hp.hpl.jena.rdf.model.Resource
import com.hp.hpl.jena.vocabulary.DC_11

import javelin.ontology.Implicits._

final
class PlainTextIndexer extends AbstractIndexer {
    lazy val logger = org.slf4j.LoggerFactory getLogger getClass

    override
    def indexFile(
        resource: ckan.Resource,
        file: java.io.File,
        mimetype: String,
        rootResource: => Resource
    ): Option[Double] =
        if (mimetype != "text/plain") None
        else {
            // logger info s"indexing plain text file: ${file.toURI} ${DC_11.title} ${file.getName}"

            val root = rootResource

            root ++= Seq(
                DC_11.title % file.getName,
                DC_11.date  % file.lastModified
            )

            // logger info s"created resource: ${root.getURI} ${root}"

            Some(.95)
        }

    override
    def indexAttachment(
        resource: conductor.ResourceAttachment,
        file: java.io.File,
        mimetype: String,
        rootResource: => Resource
    ): Option[Double] =
        if (mimetype != "text/plain") None
        else {
            // logger info s"indexing plain text file: ${file.toURI} ${DC_11.title} ${file.getName}"

            val root = rootResource

            root ++= Seq(
                DC_11.title % file.getName,
                DC_11.date  % file.lastModified
            )

            // logger info s"created resource: ${root.getURI} ${root}"

            Some(.95)
        }
}
