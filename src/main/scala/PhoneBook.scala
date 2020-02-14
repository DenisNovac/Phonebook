import cats.effect.{ContextShift, ExitCode, IO, IOApp}
import cats.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger
import org.http4s.implicits._
import org.http4s.server.Router

import scala.concurrent.ExecutionContext.Implicits.global
import org.log4s._
import pureconfig._
import pureconfig.generic.auto._
import api.CorsApiWrapper
import api.Api
import api.handlers.{IoCollectionPhoneBookHandler, IoDbPhoneBookHandler}
import share.Config
import share.ContactModel.Contact
import cats.effect._
import cats.effect.concurrent.Ref
import org.http4s.HttpRoutes


object PhoneBook extends IOApp {
  private val logger = getLogger("PhoneBook")
  implicit val cs: ContextShift[IO] = IO.contextShift(global)

  /** Парсинг конфигурационного файла */
  def getConfig(): Config = {
    ConfigSource.file("application.conf").load[Config] match {
      case Right(x) =>
        logger.info("Config file were found")
        x
      case Left(_) =>
        logger.info("No config were found, launching default")
        Config(false, "localhost", "9000", "", "", "", "", "")
    }
  }

  /** Исходя из конфига будет предпринята попытка подключиться к БД */
  def getApi(config: Config, ref: Ref[IO, List[Contact]]): Api = {
    if (config.persistent) {
      val db_handler = new IoDbPhoneBookHandler(config.dbHost, config.dbPort, config.db, config.dbUser, config.dbPassword)
      new Api(db_handler)
    } else new Api(new IoCollectionPhoneBookHandler(ref))
  }

  val config: Config = getConfig()
  logger.info(s"Application configuration: $config")


  trait PhoneApiFoo {

    def routes : HttpRoutes[IO]

  }

  class PhoneApi(p

                )

  override def run(args: List[String]): IO[ExitCode] = {
    for {
      config <- IO(getConfig())
      phonebookIo <- Ref.of[IO, List[Contact]](List())
    } yield {

      val api = getApi(config,phonebookIo)


      val apiRoutes = api.indexCall <+> api.addContact <+> api.listContacts <+> api.findContactsByName <+>
        api.findContactsByPhone <+> api.getContactById <+> api.updateContact <+> api.deleteContact <+>
        api.apiCall


      val routes = Router("/" -> apiRoutes).orNotFound
      val corsWrappedApi = CorsApiWrapper(routes)
      val loggedApi = Logger.httpApp(true, true)(corsWrappedApi)

      BlazeServerBuilder[IO]
        .bindHttp(config.appPort.toInt, config.appHost)
        .withHttpApp(loggedApi)
        .serve
        .compile
        .drain
        .as(ExitCode.Success)
    }
  }
}


