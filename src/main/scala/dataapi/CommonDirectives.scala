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

package dataapi

import scala.concurrent.ExecutionContext
import spray.routing._
import java.sql.Timestamp

/**
 * Convinience directives for creating path extractors
 * @param executionContext
 */
class CommonDirectives(implicit executionContext: ExecutionContext)
    extends Directives
    with DefaultFormats
    with dataapi.DefaultValues
{
    // Dataspace listing can be filtered on the creation time.
    val timeRestriction =
        parameter('since.as[Timestamp]?) &
        parameter('until.as[Timestamp]?)
        
    def stringToTimestamp(value: Option[String]):Option[Timestamp] =
    {
      def transform(list:List[String], value:String):Option[Timestamp] = list match{
          case Nil => throw new java.text.ParseException("Unable to parse date string!", 0)
          case x::tail => try{
                            if(x == "yyyy")
                            {
                                if(value.length == 4)
                                  Some( new Timestamp((new java.text.SimpleDateFormat(x).parse(value).getTime())))
                                else
                                  throw new java.text.ParseException("Unable to parse date string!", 0)
                            }
                            else
                              Some( new Timestamp((new java.text.SimpleDateFormat(x).parse(value).getTime())))
                              
                          }
                          catch
                          {
                           
                           case e: java.text.ParseException  => transform(tail, value)
                          }
         }
         var formats = List("yyyy-MM-dd'T'HH:mm:ssZ","yyyy-MM-dd'T'HH:mm:ss","yyyy-MM-dd", "yyyy-MM", "yyyy")
         value match {
           case Some(v) => transform(formats, v)
           case None => None
         }
         
    }
}
