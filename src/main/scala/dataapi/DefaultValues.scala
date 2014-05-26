package dataapi

import scala.concurrent.duration._

trait DefaultValues {
    implicit val timeout: akka.util.Timeout = 200.seconds

}
