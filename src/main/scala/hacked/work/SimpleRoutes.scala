package hacked.work

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.Directives

class SimpleRoutes extends Directives with JsonSerializer with JWTAuthService {

  private val login =
    path("login"){
      post {
        entity(as[User]) { user: User => {
          val validUser = verifyUser(user)
          if (validUser) {
            val claims = setClaims(user.username)
            respondWithHeader(RawHeader("Access-Token", getJwtToken(claims))) {
              complete(StatusCodes.OK)
            }
          } else {
            complete(StatusCodes.Unauthorized)
          }
        }}
      }
    }

  private val adminPanel =
    path("adminPanel") {
      get {
        authenticated { claims => {
          complete(s"Hello, Admin!")
        }}
      }
    }

  def verifyUser(user: User): Boolean ={
    user match {
      case User("admin", "password") => true
      case _ => false
    }
  }

  val routes = login ~ adminPanel

}
