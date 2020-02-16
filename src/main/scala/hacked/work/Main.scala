package hacked.work

import akka.actor.ActorSystem
import akka.http.scaladsl.Http

object Main extends App {

  implicit val system: ActorSystem = ActorSystem()
  val host: String = "localhost"
  val port: Int    = 8080

  val server = Http().bindAndHandle(new SimpleRoutes().routes, host, port)

}
