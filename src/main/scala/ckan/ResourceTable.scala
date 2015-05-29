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
    def format        : Option[String]
    def description   : Option[String]
    def name          : Option[String]
    def mimetype      : Option[String]
    def size          : Option[Long]
    def modified      : Option[Timestamp]
    def created       : Option[Timestamp]
    def packageId     : Option[String]
    def state         : Option[String]

    private def getMime = 
    {
      val tmp = java.nio.file.Paths.get(Config.Ckan.localStoragePrefix + "/" + id.substring(0,3) + "/" + id.substring(3,6) + "/" + id.substring(6))
      (mimetype getOrElse (
        if(java.nio.file.Files.probeContentType(tmp) == null)
          ""
        else
          java.nio.file.Files.probeContentType(tmp)
        )
      )
    }
    
//returns file size in KB
    private def fileSize = 
    {
      val file = new java.io.File(Config.Ckan.localStoragePrefix + "/" + id.substring(0,3) + "/" + id.substring(3,6) + "/" + id.substring(6) );
      val sz = file.length()/1024.0;
      if( sz <1.0)
        1L;
      else
        scala.math.round (sz)
    }
    
    def toJson = JsObject(
            "id"             -> JsString(id),
            "url"            -> JsString(s"${Config.namespace}resources/${id}"),
            "dataUrl"        -> JsString(s"${Config.namespace}resources/${id}/data"),
            "name"           -> JsString(name        getOrElse ""),
            "description"    -> JsString(description getOrElse ""),
            "format"         -> JsString(format      getOrElse ""),
            "mimetype"       -> JsString(getMime),
            "size"           -> JsNumber(fileSize),
            "created_epoch"  -> JsNumber(created.map  { _.getTime } getOrElse 0L),
            "modified_epoch" -> JsNumber(modified.map { _.getTime } getOrElse 0L),
            "setId"          -> JsString(packageId getOrElse ""),
            "state"          -> JsString(state getOrElse ""),
            "created"        -> JsString(created.map{_.toString} getOrElse ""),
            "modified"       -> JsString(created.map{_.toString} getOrElse "")
        )
}


case class Resource(
    id            : String,
    group         : Option[String]    = None,
    url           : String,
    format        : Option[String]    = None,
    description   : Option[String]    = None,
    position      : Option[Int]       = None,
    revisionId    : Option[String]    = None,
    hash          : Option[String]    = None,
    state         : Option[String]    = None,
    extras        : Option[String]    = None,
    name          : Option[String]    = None,
    resourceType  : Option[String]    = None,
    mimetype      : Option[String]    = None,
    mimetypeInner : Option[String]    = None,
    size          : Option[Long]      = None,
    modified      : Option[Timestamp] = None,
    created       : Option[Timestamp] = None,
    cacheUrl      : Option[String]    = None,
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
    lazy val localMimetype = mimetype getOrElse (new java.io.File(localPath).mimetype)

    lazy val isBelowSizeThreshold = {
        val fileSize = (new java.io.File(localPath)).length
        fileSize <= Config.Conductor.fileSizeLimit
    }
    
    lazy val isProcessable = isLocal // && isBelowSizeThreshold
    lazy val content = io.Source.fromFile(localPath).mkString

    override
    def toString = s"resource://$id?$accessLink"
}

case class ResourceModification(
    id            : String,
    modified      : Option[Timestamp] = None
)

class ResourceTable(tag: Tag)
    extends Table[ckan.Resource](tag, "litef_ckan_resource_view")
{
    val id            = column[ String            ]  ("id", O.PrimaryKey)
    val url           = column[ String            ]  ("url", O.NotNull)
    val group         = column[ Option[String]    ]  ("resource_group_id")
    val format        = column[ Option[String]    ]  ("format")
    val description   = column[ Option[String]    ]  ("description")
    val position      = column[ Option[Int]       ]  ("position")
    val revisionId    = column[ Option[String]    ]  ("revision_id")
    val hash          = column[ Option[String]    ]  ("hash")
    val state         = column[ Option[String]    ]  ("state")
    val extras        = column[ Option[String]    ]  ("extras")
    val name          = column[ Option[String]    ]  ("name")
    val resourceType  = column[ Option[String]    ]  ("resource_type")
    val mimetype      = column[ Option[String]    ]  ("mimetype")
    val mimetypeInner = column[ Option[String]    ]  ("mimetype_inner")
    val size          = column[ Option[Long]      ]  ("size")
    val modified      = column[ Option[Timestamp] ]  ("modified") // greatest(last_modified, created), better than just coalesce
    val created       = column[ Option[Timestamp] ]  ("created")
    val cacheUrl      = column[ Option[String]    ]  ("cache_url")
    val packageId     = column[ Option[String]    ]  ("package_id")
    val urlType       = column[ Option[String]    ]  ("url_type")

    // Every table needs a * projection with the same type as the table's type parameter
    def * = (
        id             ,
        group          ,
        url            ,
        format         ,
        description    ,
        position       ,
        revisionId     ,
        hash           ,
        state          ,
        extras         ,
        name           ,
        resourceType   ,
        mimetype       ,
        mimetypeInner  ,
        size           ,
        modified       ,
        created        ,
        cacheUrl       ,
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

