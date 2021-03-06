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
package ckan

import slick.driver.PostgresDriver.simple._
import java.sql.Timestamp
import spray.json._
import common.Config
import org.foment.utils.Filesystem._

abstract class ResourceData {
    def id            : String
    def description   : Option[String]
    def name          : Option[String]
    def mimetype      : Option[String]
    def size          : Option[Long]
    def modified      : Option[Timestamp]
    def created       : Option[Timestamp]
    def packageId     : Option[String]
    def state         : Option[String]
    
    private def getMime = {
        val tmp = java.nio.file.Paths.get(Config.Ckan.localStoragePrefix + "/" + id.substring(0,3) + "/" + id.substring(3,6) + "/" + id.substring(6))
        (mimetype getOrElse (
            if(java.nio.file.Files.probeContentType(tmp) == null) {
                ""
            } else {
                java.nio.file.Files.probeContentType(tmp)
            }
        ))
    }

    //returns file size in KB
    private def fileSize = {
        val file = new java.io.File(Config.Ckan.localStoragePrefix + "/" + id.substring(0,3) + "/" + id.substring(3,6) + "/" + id.substring(6) );

        if (!file.exists()) {
            0L
        } else {
            val sz = file.length() / 1024.0
            if (sz < 1.0) {
                1L
            } else {
                scala.math.round(sz)
            }
        }
    }

    private def formatTime(time : Option[Timestamp]) = {
        time match {
            case None => ""
            case Some(v)=> val tz = java.util.TimeZone.getTimeZone("UTC");
            val df = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
            df.setTimeZone(tz);
            df.format(new java.util.Date(v.getTime()))
        }
    }

    def viewDataUrl = {
        val encodedId = java.net.URLEncoder.encode(id, "utf-8")
        val encodedPackageId = java.net.URLEncoder.encode(packageId.getOrElse(""), "utf-8")

        s"${Config.Ckan.home}dataset/$encodedPackageId/resource/$encodedId"
    }

    def toJson = JsObject(
            "id"             -> JsString(id),
            "url"            -> JsString(s"${Config.namespace}resources/${id}"),
            "dataUrl"        -> JsString(s"${Config.namespace}resources/${id}/data"),
            "viewDataUrl"    -> JsString(viewDataUrl),
            "name"           -> JsString(name        getOrElse ""),
            "description"    -> JsString(description getOrElse ""),
            "mimetype"       -> JsString(getMime),
            "size"           -> JsNumber(fileSize),
            "created_epoch"  -> JsNumber(created.map  { _.getTime } getOrElse 0L),
            "modified_epoch" -> JsNumber(modified.map { _.getTime } getOrElse 0L),
            "setId"          -> JsString(packageId getOrElse ""),
            "state"          -> JsString(state getOrElse ""),
            "created"        -> JsString(formatTime(created)),
            "modified"       -> JsString(formatTime(modified))
        )
}


case class Resource(
    id            : String,
    url           : String,
    groupId       : Option[String]    = None,
    dataspaceId   : Option[String]    = None,
    description   : Option[String]    = None,
    state         : Option[String]    = None,
    extras        : Option[String]    = None,
    name          : Option[String]    = None,
    mimetype      : Option[String]    = None,
    size          : Option[Long]      = None,
    modified      : Option[Timestamp] = None,
    created       : Option[Timestamp] = None,
    packageId     : Option[String]    = None,
    urlType       : Option[String]    = None
) extends ResourceData {

    lazy val isLocal = urlType == Some("upload")

    // NOTE: CKAN 2.2 messes up url for local resources (in some cases url is just a file name, not a url).
    // Use accessLink instead for now
    // TODO: urlencode path segments
    lazy val accessLink = isLocal match {
        case true  => Config.Ckan.home + "dataset/" + packageId.getOrElse("") + "/resource/" + id + "/download/" + url.split("/").last
        case false => url
    }

    lazy val localPath = Config.Ckan.localStoragePrefix + "/" + id.substring(0,3) + "/" + id.substring(3,6) + "/" + id.substring(6)

    lazy val localFile = new java.io.File(localPath)

    lazy val localMimetype = {
        val result = mimetype getOrElse (new java.io.File(localPath).mimetype)
        if (result != "text/plain" && result != "text/xml" && result != "application/xml") {
            writeLog(s"Resource type is not XML it seems ${result}")
            result
        } else {
            // Unfortunately, mimetype detection is c**p, we need to detect xml
            // ourselves
            try {
                val src = scala.io.Source.fromFile(localFile)
                val reader = new scala.xml.pull.XMLEventReader(src)
                if (reader.hasNext) {
                    var rootElement: scala.xml.pull.EvElemStart = null;

                    while (rootElement == null) try {
                        rootElement = reader.next.asInstanceOf[scala.xml.pull.EvElemStart]
                    } catch {
                        case e: java.lang.ClassCastException =>
                            writeLog(s"Exception while reading class element: ${e}")
                    }

                    val rootLabel = rootElement.label.toLowerCase

                    writeLog(s"Resource root label is ${rootLabel}")

                    if (rootLabel == "rdf")
                        "application/rdf+xml"
                    else if (rootLabel contains "ead")
                        "application/ead+xml"
                    else if (rootLabel contains "eag")
                        "application/eag+xml"
                    else
                        "application/xml"
                } else
                    "text/plain"

            } catch {
                case e: Exception =>
                    writeLog(s"Tried to read the file as XML, but failed: ${e}")
                    "text/plain"
            }
        }
    }

    lazy val isBelowSizeThreshold = {
        val fileSize = (new java.io.File(localPath)).length
        fileSize <= Config.Conductor.fileSizeLimit
    }

    lazy val isProcessable = isLocal // && isBelowSizeThreshold
    lazy val content = io.Source.fromFile(localPath).mkString

    //lazy val dataspace = DataspaceResourceTable.dataspaceForResource(id)

    override
    def toString = s"resource://$id?$accessLink"

    def writeLog(s: String) {
        conductor.ResourceAttachmentUtil.writeLog(id, s)
    }
}

case class ResourceModification(
    id            : String,
    modified      : Option[Timestamp] = None
)

class ResourceTable(tag: Tag)
    extends Table[ckan.Resource](tag, "litef_ckan_resource_view")
{
    val id              = column[ String            ]  ("id", O.PrimaryKey)
    val url             = column[ String            ]  ("url", O.NotNull)
    val groupId         = column[ Option[String]    ]  ("resource_group_id")
    val dataspaceId     = column[ Option[String]    ]  ("dataspace_id")
    val description   = column[ Option[String]    ]  ("description")
    val state         = column[ Option[String]    ]  ("state")
    val extras        = column[ Option[String]    ]  ("extras")
    val name          = column[ Option[String]    ]  ("name")
    val mimetype      = column[ Option[String]    ]  ("mimetype")
    val size          = column[ Option[Long]      ]  ("size")
    val modified      = column[ Option[Timestamp] ]  ("modified") // greatest(last_modified, created), better than just coalesce
    val created       = column[ Option[Timestamp] ]  ("created")
    val packageId     = column[ Option[String]    ]  ("package_id")
    val urlType       = column[ Option[String]    ]  ("url_type")
    
    // Every table needs a * projection with the same type as the table's type parameter
    def * = (
        id             ,
        url            ,
        groupId        ,
        dataspaceId    ,
        description    ,
        state          ,
        extras         ,
        name           ,
        mimetype       ,
        size           ,
        modified       ,
        created        ,
        packageId      ,
        urlType
    ) <> (Resource.tupled, Resource.unapply)
}

object ResourceTable {
    val query = TableQuery[ResourceTable]
}

object ResourceJsonProtocol extends DefaultJsonProtocol {
    implicit object ResourceJsonFormat extends RootJsonFormat[Resource] {
        def write(rs: Resource) = rs.toJson

        def read(value: JsValue) = {
            throw new DeserializationException("Resource can not be read from JSON")
        }
    }

    implicit object ResourceSeqJsonFormat extends RootJsonFormat[List[Resource]] {
        def write(ds: List[Resource]) =
            JsArray(ds.map{ _.toJson })

        def read(value: JsValue) =
            throw new DeserializationException("Resource can not be read from JSON")
    }
}

