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

import akka.actor.Actor
import akka.io.IO
import akka.pattern.ask
import spray.can.Http
import spray.http._
import spray.httpx.RequestBuilding._
import spray.httpx.SprayJsonSupport._
import spray.json._
import DefaultJsonProtocol._
import MediaTypes._
import HttpCharsets._
import HttpMethods._
import HttpHeaders._

import conductor.ResourceAttachmentUtil._

import common.Config.{ Nerd => NerdConfig }
import java.sql.Timestamp

class ElasticFeederPlugin extends AbstractPluginActor("ElasticFeeder")
{
    import context.system

    lazy val logger = org.slf4j.LoggerFactory getLogger getClass
    lazy val elasticIndexer = fr.inria.aviz.elasticindexer.Indexer.instance

    override
    def process(resource: ckan.Resource) = Future {
    }

    override
    def process(attachment: conductor.ResourceAttachment) = Future {
        try {
            if (attachment.format == "application/x-elasticindexer-json-output") {

                val contents = scala.io.Source.fromFile(attachment.localPath).mkString

                logger info s"Sending attachment to Elastic: ${attachment.resourceId}, ${attachment.format}"
                elasticIndexer.indexDocument(contents)

            }
        } catch {
            case e: Exception => e.printStackTrace
        }
    }
}

