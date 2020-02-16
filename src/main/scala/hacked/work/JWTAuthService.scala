package hacked.work

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directive1, Directives}
import org.json4s.{DefaultFormats, JObject, JString}
import pdi.jwt.{JwtAlgorithm, JwtJson4s}

trait JWTAuthService extends Directives {

  implicit val formats = DefaultFormats
  private val secretKey = "Your.Super.Secret.Key"
  private val algo = JwtAlgorithm.HS512

  def getJwtToken(claims: JObject): String = {
    JwtJson4s.encode(claims, secretKey, algo)
  }

  def setClaims(user: String): JObject =
    JObject(
      ("user", JString(user)),
      ("whatever", JString("you can store anything"))
    )

  private def getClaims(jwt: String): Map[String, String] ={
    JwtJson4s.decodeJson(jwt, secretKey, Seq(algo)).toOption.map(j => j.extract[Map[String, String]]).getOrElse(Map.empty)
  }

  def authenticated: Directive1[Map[String, Any]] =
    optionalHeaderValueByName("Access-Token").flatMap {
      case Some(jwt) =>
        val claim = getClaims(jwt)
        if(claim.nonEmpty && claim.get("user").nonEmpty){
           provide(claim)
         } else complete(StatusCodes.Unauthorized)
      case _ => complete(StatusCodes.Unauthorized)
    }

}
