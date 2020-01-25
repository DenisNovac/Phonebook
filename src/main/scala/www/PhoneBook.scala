package www

import cats.effect._
import cats.implicits._

import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.implicits._
import Api._
import scala.concurrent.ExecutionContext.Implicits.global

object PhoneBook extends IOApp{

  val port = 9000
  val host = "172.18.1.2"
  //val host = "localhost"

  val calls = indexCall <+> addContact <+> listContacts <+> findContactsByName <+> findContactsByPhone <+>
    getContactById <+> updateContact <+> deleteContact <+> apiCall
  val apiCalls = Router("/" -> calls).orNotFound
  val wrapper = CorsApiWrapper(apiCalls)

  //override implicit val timer: Timer[IO] = IO.timer(global)
  implicit val cs: ContextShift[IO] = IO.contextShift(global)
  override def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO]
      .bindHttp(port, host)
      .withHttpApp(wrapper)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
}
