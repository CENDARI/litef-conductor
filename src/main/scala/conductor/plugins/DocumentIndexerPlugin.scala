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

class DocumentIndexerPlugin extends AbstractPluginActor("Litef Indexer")
{

    override
    def process(resource: ckan.Resource): Future[Unit] = {
        if(resource.state == Some("active")) Future { indexer.IndexingManager index resource }
        else Future {}
    }
    
    override
    def process(attachment: conductor.ResourceAttachment) = Future {
        indexer.IndexingManager index attachment
    }
}

