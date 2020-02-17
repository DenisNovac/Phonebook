import cats.effect.{ExitCode, IO, IOApp}
import org.http4s.server.blaze.BlazeServerBuilder
import cats.implicits._

object Main extends IOApp {

  private val initModule = new InitModule[IO]
  private val config = initModule.config

  override def run(args: List[String]): IO[ExitCode] = {
    BlazeServerBuilder[IO]
        .bindHttp(config.appPort.toInt, config.appHost)
        .withHttpApp(initModule.api)
        .serve
        .compile
        .drain
        .as(ExitCode.Success)
  }
}



