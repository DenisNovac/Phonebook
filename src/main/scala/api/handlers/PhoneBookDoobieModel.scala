package api.handlers


import java.sql.SQLException

import cats.Monad
import cats.effect._
import doobie._
import doobie.implicits._
import model.PhoneBookApiModel
import model.ContactModel._
import org.log4s.getLogger



/** Класс, в котором собраны методы для работы с БД PostgreSQL (через Doobie).
  * Может заменять собой IoCollectionPhoneBookHandler ввиду одного интерфейса.
  */
class PhoneBookDoobieModel[F[_]: Async](tr: Transactor[F]) extends PhoneBookApiModel[F] {
  private val logger = getLogger("PhoneBookDoobieModel")


  private def getBook(): F[List[Contact]] =
    sql"SELECT * FROM phonebook"
      .query[Contact]
      .to[List]
      .transact(tr)


  override def addContact(c: ContactRequest): F[Int] = {
    logger.info("sdofksdofkosdkf")
    logger.info(s"${c.name}   ${c.phoneNumber}")
    sql"INSERT INTO phonebook (name, phoneNumber) VALUES (${c.name}, ${c.phoneNumber})"
      .update
      .run
      .transact(tr)
  }


  override def listContacts(): F[List[Contact]] =
    getBook()


  override def findContactByName(name: List[String]): F[List[Contact]] = {
    val query = name.mkString("% OR name LIKE ")
    sql"SELECT * FROM phonebook WHERE name LIKE ${query+"%"}"
      .query[Contact]
      .to[List]
      .transact(tr)
  }


  override def findContactByPhone(phone: List[String]): F[List[Contact]]= {
    val query = phone.mkString("% OR phoneNumber LIKE ")
    sql"SELECT * FROM phonebook WHERE phoneNumber LIKE ${query+"%"}"
      .query[Contact]
      .to[List]
      .transact(tr)
  }


  override def getContactById(id: Long): F[Option[Contact]] =
    sql"SELECT * FROM phonebook WHERE id = $id"
                  .query[Contact]
                  .option
                  .transact(tr)


  override def updateContact(id: Long, body: ContactRequest): F[Int] =
    sql"UPDATE phonebook SET name=${body.name}, phoneNumber=${body.phoneNumber} WHERE id=${id}"
      .update
      .run
      .transact(tr)


  override def deleteContact(id: Long): F[Int] =
    sql"DELETE FROM phonebook WHERE id=${id}"
      .update
      .run
      .transact(tr)

}
