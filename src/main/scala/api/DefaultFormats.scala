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

package api

import spray.json._
import java.util.UUID
import scala.reflect.ClassTag
import spray.httpx.marshalling.{MetaMarshallers, Marshaller, CollectingMarshallingContext}
import spray.http.StatusCode
import spray.httpx.SprayJsonSupport
import java.sql.Timestamp
import spray.httpx.unmarshalling.{MalformedContent, DeserializationError, FromStringDeserializer, FromStringDeserializers}

// Contains implicits and other ugly stuff needed for akka/spray
// to know about the data types we are using
trait DefaultFormats extends DefaultJsonProtocol with SprayJsonSupport with MetaMarshallers {

    // Conversion between objects and json
    def jsonObjectFormat[A : ClassTag]: RootJsonFormat[A] = new RootJsonFormat[A] {
        val ct = implicitly[ClassTag[A]]
        def write(obj: A): JsValue = JsObject("value" -> JsString(ct.runtimeClass.getSimpleName))
        def read(json: JsValue): A = ct.runtimeClass.newInstance().asInstanceOf[A]
    }

    // Conversion between uuids and json
    implicit object UuidJsonFormat extends RootJsonFormat[UUID] {
        def write(x: UUID) = JsString(x.toString)
        def read(value: JsValue) = value match {
            case JsString(x) => UUID.fromString(x)
            case x           => deserializationError("Expected UUID as JsString, but got " + x)
        }
    }

    // Conversion between timestamps and json
    implicit object TimestampFormat extends RootJsonFormat[Timestamp] {
        def write(obj: Timestamp) = JsNumber(obj.getTime)

        def read(json: JsValue) = json match {
            case JsNumber(time) => new Timestamp(time.toLong)

            case _ => throw new DeserializationException("Date expected")
        }
    }

    // Implicit converter from strings to timestamps
    implicit val stringToTimestamp = new FromStringDeserializer[Timestamp] {
        def apply(value: String): Either[DeserializationError, Timestamp] =
            try {
                Right(Timestamp valueOf value)
            } catch {
                case e: IllegalArgumentException =>
                    Left(MalformedContent(s"Failed to convert the string '$value' to a timestamp", e))
            }
    }


    /**
     * Type alias for function that converts ``A`` to some ``StatusCode``
     * @tparam A the type of the input values
     */
    type ErrorSelector[A] = A => StatusCode

    /**
     * Marshals instances of ``Either[A, B]`` into appropriate HTTP responses by marshalling the values
     * in the left or right projections; and by selecting the appropriate HTTP status code for the
     * values in the left projection.
     *
     * @param ma marshaller for the left projection
     * @param mb marshaller for the right projection
     * @param esa the selector converting the left projection to HTTP status code
     * @tparam A the left projection
     * @tparam B the right projection
     * @return marshaller
     */
    implicit def errorSelectingEitherMarshaller[A, B](implicit ma: Marshaller[A], mb: Marshaller[B], esa: ErrorSelector[A]): Marshaller[Either[A, B]] =
        Marshaller[Either[A, B]] { (value, ctx) =>
            value match {
                case Left(a) =>
                    val mc = new CollectingMarshallingContext()
                    ma(a, mc)
                    ctx.handleError(ErrorResponseException(esa(a), mc.entity))
                case Right(b) =>
                    mb(b, ctx)
            }
        }

}
