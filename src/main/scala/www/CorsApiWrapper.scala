package www

import cats.Id
import cats.data.Kleisli
import cats.effect._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.server.middleware._
import scala.concurrent.duration._

/**
  * Cross-origin resource sharing (CORS) - технология, предоставляющая веб-странице доступ к
  * ресурсам другого домена. Эта обёртка над апи необходима для выполнения запросов через Swagger.
  */
object CorsApiWrapper {

  val methodConfig = CORSConfig(
    anyOrigin = true,
    anyMethod = true,
    allowCredentials = false,
    maxAge = 1.day.toSeconds)

  def apply(service: Kleisli[IO, Request[IO], Response[IO]]): HttpApp[IO] =
    CORS(service)
}
