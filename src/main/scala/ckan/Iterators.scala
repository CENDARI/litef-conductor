/*
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

import java.sql.Timestamp
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.BASE64EncoderStream
import sun.misc.{BASE64Decoder, BASE64Encoder}
import java.nio.ByteBuffer
import scala.util.Try
import dataapi.StateFilter
import dataapi.StateFilter.StateFilter
//import dataapi.VisibilityFilter
//import dataapi.VisibilityFilter.VisibilityFilter

/**
* A convenience class to manage the chunked responses without explicitely
* exposing the start/offset mechanism
* @param since
* @param until
* @param state
* @param start
* @param count
*/
case class IteratorData(val since: Timestamp, val until: Timestamp, val state: StateFilter, val start: Int, val count: Int) {
    def generateId: String = {
        val since = this.since.getTime
        val until = this.until.getTime

        val bytes = ByteBuffer.allocate(8 * 2 + 4 * 3)
                        .putLong(since)
                        .putLong(until)
                        .putInt(state.id)
                        .putInt(start)
                        .putInt(count)
                        .array

        IteratorData.base64encoder.encode(bytes)
    }
}


object IteratorData {
    val base64encoder = new BASE64Encoder()
    val base64decoder = new BASE64Decoder()

    def fromId(id: String) = Try {
        val raw = base64decoder.decodeBuffer(id)
        val bytes = ByteBuffer.wrap(raw)

        val since = bytes.getLong
        val until = bytes.getLong
        val state = bytes.getInt
        val start = bytes.getInt
        val count = bytes.getInt

        IteratorData(
            new Timestamp(since),
            new Timestamp(until),
            StateFilter(state),
            start,
            count)
    }
}

//case class PackageIteratorData(val since: Timestamp, val until: Timestamp, val visibility: VisibilityFilter, val state: StateFilter, val start: Int, val count: Int) {
//    def generateId: String = {
//        val since = this.since.getTime
//        val until = this.until.getTime
//        
//        val bytes = ByteBuffer.allocate(8 * 2 + 4 * 4)
//                        .putLong(since)
//                        .putLong(until)
//                        .putInt(visibility.id)
//                        .putInt(state.id)
//                        .putInt(start)
//                        .putInt(count)
//                        .array
//
//        IteratorData.base64encoder.encode(bytes)
//    }
//}
//
//
//object PackageIteratorData {
//    val base64encoder = new BASE64Encoder()
//    val base64decoder = new BASE64Decoder()
//
//    def fromId(id: String) = Try {
//        val raw = base64decoder.decodeBuffer(id)
//        val bytes = ByteBuffer.wrap(raw)
//
//        val since = bytes.getLong
//        val until = bytes.getLong
//        val visibility = bytes.getInt
//        val state = bytes.getInt
//        val start = bytes.getInt
//        val count = bytes.getInt
//
//        PackageIteratorData(
//            new Timestamp(since),
//            new Timestamp(until),
//            VisibilityFilter(visibility),
//            StateFilter(state),
//            start,
//            count)
//    }
//}