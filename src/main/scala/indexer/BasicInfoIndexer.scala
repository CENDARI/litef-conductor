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

package indexer

import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.rdf.model.Resource
import nepomuk.ontology.NAO
import org.foment.utils.Exceptions._
import javelin.ontology.Implicits._

import com.hp.hpl.jena.vocabulary.DC_11

final
class BasicInfoIndexer extends AbstractIndexer {
    override
    def indexFile(
        resource: ckan.Resource,
        file: java.io.File,
        mimetype: String,
        root: => Resource
    ): Option[Double] = exceptionless (
        getBasicInfo(resource, file, root),
        "BasicInfoIndexer: We can not fail"
    )

    // We are not getting the info about attachments
    // def indexAttachment

    def getBasicInfo(resource: ckan.Resource, file: java.io.File, root: => Resource) = {
        root ++= Seq(
            NAO.lastModified % file.lastModified,
            NAO.identifier   % resource.id,
            DC_11.source     % file.getName
        )

        Some(1.0)
    }
}
