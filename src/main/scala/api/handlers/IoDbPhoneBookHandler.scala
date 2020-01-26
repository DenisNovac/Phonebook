package api.handlers

import doobie._
import doobie.implicits._
import doobie.util.compat._
import doobie.postgres.implicits._
import doobie.util.ExecutionContexts
import cats.effect.IO
import org.http4s.Response
import org.postgresql.util.PSQLException
import share.ContactModel
import org.log4s
import java.sql.SQLTimeoutException

/** Класс, в котором собраны методы для работы с БД PostgreSQL (через Doobie).
  * Может заменять собой IoCollectionPhoneBookHandler ввиду одного интерфейса.
  */
class IoDbPhoneBookHandler extends ApiPhoneBookHandler {
  private val logger = log4s.getLogger("IoDbPhoneBookHandler")

  /** Контекст для транзактора */
  private implicit val cs = IO.contextShift(ExecutionContexts.synchronous)

  private val driver = "org.postgresql.Driver"
  //private val connectionString = "jdbc:postgresql:172.18.1.10:postgres"
  private val connectionString = "jdbc:postgresql:postgres"
  private val user = "postgres"
  private val pass = "P@ssw0rd"

  private lazy val tr = Transactor.fromDriverManager[IO](driver, connectionString, user, pass)

  /** При создании этого класса будет автоматически создана нужная таблица.
    * Если БД недоступна - будет предпринято несколько попыток достучаться до неё.
    */
  private val initTable = waitDb(1)

  private def waitDb(attempt: Int): Unit = {
    val maxRetries = 10
    val retryTimeMs = 5000

    if (attempt > maxRetries) throw new SQLTimeoutException("Can't reach database")
    try {
      sql"CREATE TABLE IF NOT EXISTS phonebook (id SERIAL PRIMARY KEY,name VARCHAR(100) NOT NULL,phoneNumber VARCHAR(100) NOT NULL);"
        .update
        .run
        .transact(tr)
        .unsafeRunSync()
      logger.info(s"Database connection established")
    } catch {
      case ex: PSQLException =>
        logger.error(s"Can't reach database on $attempt/$maxRetries attempt, retry in ${retryTimeMs/1000} secs: ${ex}")
        Thread.sleep(retryTimeMs)
        waitDb(attempt + 1)
    }
  }




  override def addContactIo(c: ContactModel.ContactRequest): IO[Response[IO]] = ???

  override def listContactsIo(): IO[Response[IO]] = ???

  override def findContactByNameIo(name: List[String]): IO[Response[IO]] = ???

  override def findContactByPhoneIo(phone: List[String]): IO[Response[IO]] = ???

  override def getContactByIdIo(id: Long): IO[Response[IO]] = ???

  override def updateContactIo(id: Long, body: ContactModel.ContactRequest): IO[Response[IO]] = ???

  override def deleteContactIo(id: Long): IO[Response[IO]] = ???
}
