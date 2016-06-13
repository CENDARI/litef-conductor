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

class NerdPlugin extends AbstractPluginActor("NERD")
{
    import context.system

    // lazy val logger = org.slf4j.LoggerFactory getLogger getClass

    def nerdProcess[T](dataFile: String): Future[HttpResponse] = {
        val data = java.net.URLEncoder.encode(scala.io.Source.fromFile(dataFile).mkString, "utf-8")
        
        (IO(Http) ? (
            Post(NerdConfig.namespace + "processNERDText?onlyNER=true&text=" + data)
            )).mapTo[HttpResponse]
    }
    
//    def nerdMultilingProcess[T](dataFile: String): Future[HttpResponse] = {
//        val data = scala.io.Source.fromFile(dataFile).mkString
//        val requestData = Map("text" -> data)
//        
//        (IO(Http) ? (
//            Post(NerdConfig.namespaceMultilingual + "processNERDText", FormData(requestData))
//            )).mapTo[HttpResponse]
//    }
    
    def saveResponse(
            resourceId: String,
            resourceCreated: Option[Timestamp],
            resourceModified: Option[Timestamp],
            response: HttpResponse) {
        indexer.AbstractIndexer.saveAttachment(
            resourceId,
            resourceCreated,
            resourceModified,
            response.entity.asString,
            "application/x-nerd-output"
        )
    }

    override
    def process(resource: ckan.Resource) = {
        if (resource.mimetype == "text/plain" && resource.state == Some("active")) {
            val fileSize = (new java.io.File(resource.localPath)).length
            
            if(fileSize > NerdConfig.fileSizeLimit) {
                Future { resource writeLog s"NerdPlugin: Size limit exceeded. Resource size is $fileSize bytes" }
            }
            else {
                resource writeLog "NerdPlugin: Sending content to the English NERD service"
                
                nerdProcess(resource.localPath)
                    .map { response => response.status match {
                    case StatusCodes.OK =>
                        saveResponse(resource.id,
                                     resource.created,
                                     resource.modified,
                                     response)
                        resource writeLog s"\t -> English NERD Success"

                    case _ =>
                        resource writeLog s"\t -> English NERD Error \n${response.entity.asString}"
//                        resource writeLog "\t -> Sending content to the Multilingual NERD service" 
//                       
//                        nerdMultilingProcess(resource.localPath)
//                            .map { response => response.status match {
//                            case StatusCodes.OK =>
//                                saveResponse(resource.id,
//                                             resource.created,
//                                             resource.modified,
//                                             response)
//                                resource writeLog "\t -> Multilingual NER Success"
//
//                            case _ =>
//                                resource writeLog s"\t -> Multilingual NER Error \n${response.entity.asString}"
//
//                            }
//                        }
                    }
                }
            }
        } else Future {}
    }


    override
    def process(attachment: conductor.ResourceAttachment) = {
        if (attachment.format == "text/plain") {
            val fileSize = (new java.io.File(attachment.localPath)).length
            
            if(fileSize > NerdConfig.fileSizeLimit) {
                Future { attachment writeLog s"NerdPlugin: Size limit exceeded. Attachment size is $fileSize bytes" }
            }
            else {
                attachment writeLog "NerdPlugin: Sending content to the English NERD service"
                
                nerdProcess(attachment.localPath)
                    .map { response => response.status match {
                    case StatusCodes.OK =>
                        saveResponse(attachment.resourceId,
                                     Some(attachment.created),
                                     Some(attachment.modified),
                                     response)
                        attachment writeLog "\t -> English NERD Success"

                    case _ =>
                        attachment writeLog s"\t -> English NERD Error \n${response.entity.asString}"
//                        attachment writeLog "\t -> Sending content to the Multilingual NERD service"
//                
//                        nerdMultilingProcess(attachment.localPath)
//                            .map { response => response.status match {
//                            case StatusCodes.OK =>
//                                saveResponse(attachment.resourceId,
//                                             Some(attachment.created),
//                                             Some(attachment.modified),
//                                             response)
//                                attachment writeLog s"\t -> Multilingual NERD Success"
//
//                            case _ =>
//                                attachment writeLog s"\t -> Multilingual NERD Error \n${response.entity.asString}"
//
//                            }
//                        }
                    }
                }
            }
        } else Future {}
    }
}

