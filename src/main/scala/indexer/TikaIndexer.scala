/*
 * Copyright (C) 2015 Ivan Cukic <ivan at mi.sanu.ac.rs>
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

import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.rdf.model.Resource
import org.foment.utils.Exceptions._
import nepomuk.ontology.NIE

import javelin.ontology.Implicits._
import com.hp.hpl.jena.vocabulary.DC_11

final
class TikaIndexer extends AbstractIndexer {
    override
    def indexFile(
        resource: ckan.Resource,
        file: java.io.File,
        mimetype: String,
        root: => Resource
    ): Option[Double] = exceptionless (
        runTika(resource, file, root),
        "We can not fail"
    )

    lazy val tikaIndexer = fr.inria.aviz.elasticindexer.Indexer.instance

    def runTika(resource: ckan.Resource, file: java.io.File, root: => Resource) = {

        val info = tikaIndexer.parseDocument(file.getName, null, new java.io.FileInputStream(file), -1)

        AbstractIndexer.saveAttachment(resource, info.getText, "text/plain")
        AbstractIndexer.saveAttachment(resource, tikaIndexer toJSON info, "application/x-elasticindexer-json-output")

        // TODO: We might want to save the plain text in Virtuoso,
        // or other fields
        root ++= Seq(
            NIE.plainTextContent % info.getText,
            DC_11.title          % info.getTitle,
            DC_11.creator        % info.getCreator,
            DC_11.date           % info.getDate,
            DC_11.format         % info.getFormat
        )
        Some(0.85)
    }

    override
    def indexAttachment(
        resource: conductor.ResourceAttachment,
        file: java.io.File,
        mimetype: String,
        root: => Resource
    ): Option[Double] = None
}
