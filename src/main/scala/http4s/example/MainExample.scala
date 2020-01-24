package http4s.example

import cats.effect._
import cats.implicits._

import org.http4s.HttpRoutes
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.server.blaze._
import org.http4s.server.Router
import org.http4s.circe._

import io.circe.syntax._

import phonebook.ContactHandler._

object MainExample extends IOApp {

  val indexService = HttpRoutes.of[IO] {
    case GET -> Root =>
      Ok("Phonebook works")
  }

  object NameParser extends QueryParamDecoderMatcher[String]("name")  // достаёт переменную из куска запроса

  val helloWorldService = HttpRoutes.of[IO] {
    case GET -> Root / "hello" :? NameParser(name) =>
      Ok(s"Hello, $name")
  }

  val getJsonWithContact = HttpRoutes.of[IO] {
    case GET -> Root / "contact" =>
      Ok(Contact(1,"Denis","123").asJson)  // import org.http4s.circe._ позволяет так просто возвращать JSON-ы
  }

  val services = helloWorldService <+> indexService <+> getJsonWithContact
  val httpApp = Router("/" -> indexService, "/api" -> services).orNotFound  // порядок не имеет значения

  def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO]
      .bindHttp(9000, "172.18.1.2")
      .withHttpApp(httpApp)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
}
