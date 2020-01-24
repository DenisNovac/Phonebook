package www

import cats.effect._
import cats.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.implicits._
import Api._
import cats.data.Kleisli
import org.http4s.{Request, Response}

object Phonebook extends IOApp{

  val port = 9000
  val host = "172.18.1.2"

  val calls = indexCall <+> addContact
  val apiCalls: Kleisli[IO, Request[IO], Response[IO]] = Router("/" -> calls).orNotFound
  val wrapper = CorsApiWrapper(apiCalls)

  override def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO]
      .bindHttp(port, host)
      .withHttpApp(wrapper)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
}
