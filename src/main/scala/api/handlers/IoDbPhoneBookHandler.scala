package api.handlers

import cats._
import cats.data._
import cats.effect._
import cats.effect.IO
import cats.effect.implicits._

import doobie._
import doobie.implicits._
import doobie.util.compat._
import doobie.postgres.implicits._
import doobie.util.ExecutionContexts

import io.circe.syntax._
import org.http4s.circe._
import org.http4s.Response
import org.http4s.dsl.io._
import org.postgresql.util.PSQLException

import org.log4s
import java.sql.SQLTimeoutException
import scala.annotation.tailrec

import share.PhoneBookModel._
import share.ContactModel._
import share.InputValidator._



/** Класс, в котором собраны методы для работы с БД PostgreSQL (через Doobie).
  * Может заменять собой IoCollectionPhoneBookHandler ввиду одного интерфейса.
  */
class IoDbPhoneBookHandler(host: String, port: String, db: String,
                           user: String, password: String ) extends ApiPhoneBookHandler {

  private val logger = log4s.getLogger("IoDbPhoneBookHandler")

  /** Контекст для транзактора */
  private implicit val cs = IO.contextShift(ExecutionContexts.synchronous)

  private val driver = "org.postgresql.Driver"
  private val connectionString = s"jdbc:postgresql://$host:$port/$db"
  //private val connectionString = "jdbc:postgresql://172.18.1.10:5432/postgres"

  private lazy val tr = Transactor.fromDriverManager[IO](driver, connectionString, user, password)

  /** При создании этого класса будет автоматически создана нужная таблица.
    * Если БД недоступна - будет предпринято несколько попыток достучаться до неё.
    */
  private val initTable = waitDb()

  /** Функция-цикл для попыток переподключений к БД через интервалы времени.
    * @param attempt номер первой попытки
    */
  @tailrec
  private def waitDb(attempt: Int = 1): Unit = {
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


  private def getBook(): PhoneBook =
    sql"SELECT * FROM phonebook"
      .query[Contact]
      .to[List]
      .transact(tr)
      .unsafeRunSync()


  override def addContactIo(c: ContactRequest): IO[Response[IO]] = {
    if (isNameValid(c.name) & isPhoneValid(c.phoneNumber)) {
      sql"INSERT INTO phonebook (name, phoneNumber) VALUES (${c.name}, ${c.phoneNumber})"
        .update
        .run
        .transact(tr)
        .unsafeRunSync()
      Ok(PhoneBookModel(getBook()).asJson)
    } else BadRequest("Invalid input")
  }

  override def listContactsIo(): IO[Response[IO]] =
    Ok(PhoneBookModel(getBook()).asJson)

  override def findContactByNameIo(name: List[String]): IO[Response[IO]] = {
    if (name forall(n => isNameValid(n))) {
      val found = for {
        n <- name
        r <- sql"SELECT * FROM phonebook WHERE name LIKE ${n+"%"}"
              .query[Contact]
              .to[List]
              .transact(tr)
              .unsafeRunSync()
      } yield r

      Ok(PhoneBookModel(found).asJson)

    } else BadRequest("Invalid name value")
  }


  override def findContactByPhoneIo(phone: List[String]): IO[Response[IO]] = {
    if (phone forall(n => isNameValid(n))) {
      val found = for {
        p <- phone
        r <- sql"SELECT * FROM phonebook WHERE phoneNumber LIKE ${p+"%"}"
          .query[Contact]
          .to[List]
          .transact(tr)
          .unsafeRunSync()
      } yield r

      Ok(PhoneBookModel(found).asJson)

    } else BadRequest("Invalid phone value")
  }

  override def getContactByIdIo(id: Long): IO[Response[IO]] = {
    val found = sql"SELECT * FROM phonebook WHERE id = $id"
                  .query[Contact]
                  .option
                  .transact(tr)
                  .unsafeRunSync()
    found match {
      case Some(x) =>Ok(x.asJson)
      case None => NotFound("Contact not found")
    }
  }


  override def updateContactIo(id: Long, body: ContactRequest): IO[Response[IO]] = {
    if (isNameValid(body.name) & isPhoneValid(body.phoneNumber)) {
      val res = sql"UPDATE phonebook SET name=${body.name}, phoneNumber=${body.phoneNumber} WHERE id=${id}"
        .update
        .run
        .transact(tr)
        .unsafeRunSync()
      res match {
        case 1 => Ok(PhoneBookModel(getBook()).asJson)
        case 0 => NotFound("Contact not found")
      }
    } else BadRequest("Invalid input")
  }


  override def deleteContactIo(id: Long): IO[Response[IO]] = {
    val res = sql"DELETE FROM phonebook WHERE id=${id}"
      .update
      .run
      .transact(tr)
      .unsafeRunSync()
    res match {
      case 1 => Ok(PhoneBookModel(getBook()).asJson)
      case 0 => NotFound("Contact not found")
    }
  }



  /** Для тестов */
  def resetTable(): Unit = {
    sql"DROP TABLE IF EXISTS phonebook"
      .update
      .run
      .transact(tr)
      .unsafeRunSync()
    sql"CREATE TABLE IF NOT EXISTS phonebook (id SERIAL PRIMARY KEY,name VARCHAR(100) NOT NULL,phoneNumber VARCHAR(100) NOT NULL);"
      .update
      .run
      .transact(tr)
      .unsafeRunSync()
  }

}
