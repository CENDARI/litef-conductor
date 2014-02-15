package api

import scala.concurrent.duration._

trait DefaultValues {
    implicit val timeout: akka.util.Timeout = 25.seconds

}
