package api.handlers

import cats._
import cats.data._
import cats.implicits._

import cats.{Foldable, Monad}
import cats.kernel.Monoid
import cats.effect._
import doobie._
import doobie.implicits._
import model.PhoneBookApiModel
import model.ContactModel._
import org.log4s.getLogger

/** Класс, в котором собраны методы для работы с БД PostgreSQL (через Doobie).
  * Может заменять собой IoCollectionPhoneBookHandler ввиду одного интерфейса.
  */
class PhoneBookDoobieModel[F[_]: Sync: Monad](tr: Transactor[F]) extends PhoneBookApiModel[F] {
  private val logger = getLogger("PhoneBookDoobieModel")

  // CREATE TABLE IF NOT EXISTS phonebook (id SERIAL PRIMARY KEY,name VARCHAR(100) NOT NULL,phoneNumber VARCHAR(100) NOT NULL);

  private def getBook(): F[List[Contact]] = {
    logger.info(s"SELECT * FROM phonebook")
    sql"SELECT * FROM phonebook"
      .query[Contact]
      .to[List]
      .transact(tr)
  }

  override def addContact(c: ContactRequest): F[Int] = {
    logger.info(s"INSERT INTO phonebook (name, phoneNumber) VALUES (${c.name}, ${c.phoneNumber})")
    sql"INSERT INTO phonebook (name, phoneNumber) VALUES (${c.name}, ${c.phoneNumber})".update.run
      .transact(tr)
  }

  override def listContacts(): F[List[Contact]] =
    getBook()

  override def findContactByName(name: List[String]): F[List[Contact]] = {
    val queries: List[F[List[Contact]]] = for {
      n <- name
    } yield {
      logger.info(s"SELECT * FROM phonebook WHERE name LIKE ${n + "%"}")
      sql"SELECT * FROM phonebook WHERE name LIKE ${n + "%"}"
        .query[Contact]
        .to[List]
        .transact(tr)
    }
    queries.flatSequence
  }

  override def findContactByPhone(phone: List[String]): F[List[Contact]] = {
    val queries: List[F[List[Contact]]] = for {
      p <- phone
    } yield {
      logger.info(s"SELECT * FROM phonebook WHERE phoneNumber LIKE ${p + "%"}")
      sql"SELECT * FROM phonebook WHERE phoneNumber LIKE ${p + "%"}"
        .query[Contact]
        .to[List]
        .transact(tr)
    }
    queries.flatSequence
  }

  override def getContactById(id: Long): F[Option[Contact]] = {
    logger.info(s"SELECT * FROM phonebook WHERE id = $id")
    sql"SELECT * FROM phonebook WHERE id = $id"
      .query[Contact]
      .option
      .transact(tr)
  }

  override def updateContact(id: Long, body: ContactRequest): F[Int] = {
    logger.info(s"UPDATE phonebook SET name=${body.name}, phoneNumber=${body.phoneNumber} WHERE id=${id}")
    sql"UPDATE phonebook SET name=${body.name}, phoneNumber=${body.phoneNumber} WHERE id=${id}".update.run
      .transact(tr)
  }

  override def deleteContact(id: Long): F[Int] = {
    logger.info(s"DELETE FROM phonebook WHERE id=${id}")
    sql"DELETE FROM phonebook WHERE id=${id}".update.run
      .transact(tr)
  }

}
