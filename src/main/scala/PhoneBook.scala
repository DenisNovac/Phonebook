import api.Api.indexCall

import cats.effect.{ContextShift, ExitCode, IO, IOApp}
import cats.implicits._

import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger
import org.http4s.implicits._
import org.http4s.server.Router

import scala.concurrent.ExecutionContext.Implicits.global

import api.CorsApiWrapper
import api.Api._


object PhoneBook extends IOApp{
  implicit val cs: ContextShift[IO] = IO.contextShift(global)

  val port = 9000
  val host = "172.18.1.2"
  //val host = "localhost"

  val calls = indexCall <+> addContact <+> listContacts <+> findContactsByName <+> findContactsByPhone <+>
    getContactById <+> updateContact <+> deleteContact <+> apiCall

  val api = Router("/" -> calls).orNotFound
  val corsWrappedApi = CorsApiWrapper(api)
  val loggedApi = Logger.httpApp(true, true)(corsWrappedApi)


  override def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO]
      .bindHttp(port, host)
      .withHttpApp(loggedApi)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
}
