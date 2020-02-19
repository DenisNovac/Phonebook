import api.{Api, CorsApiWrapper}
import api.handlers.PhoneBookDoobieModel
import cats.effect.{Async, Concurrent, ContextShift, Sync}
import config.ConfigModule
import doobie._
import org.http4s.HttpApp
import org.http4s.server.middleware.Logger
import cats.implicits._

class InitModule[F[_]: Sync](implicit contextShift: ContextShift[F], implicit val concurrent: Concurrent[F])
    extends ConfigModule {
  logConfig()
  private lazy val driver           = "org.postgresql.Driver"
  private lazy val connectionString = s"jdbc:postgresql://${config.dbHost}:${config.dbPort}/${config.db}"

  private lazy val transactor: Transactor[F] =
    Transactor.fromDriverManager[F](driver, connectionString, config.dbUser, config.dbPassword)
  private lazy val db = new PhoneBookDoobieModel[F](transactor)

  private lazy val rawApi         = new Api(db)
  private lazy val corsWrappedApi = CorsApiWrapper(rawApi.routes)
  val api: HttpApp[F]             = Logger.httpApp(true, true)(corsWrappedApi)
}
