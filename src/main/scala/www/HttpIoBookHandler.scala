package www

import cats.effect.IO
import cats.effect.concurrent.Ref
import io.circe.syntax._
import org.http4s.Response
import org.http4s.dsl.io._
import org.http4s.circe._
import phonebook.ContactHandler._
import phonebook.PhoneBookHandler._
import phonebook.{ContactNotFound, InvalidIdSupplied, InvalidInput, InvalidNameValue, InvalidPhoneValue}

object HttpIoBookHandler {

  // базовая телефонная книга, ссылка на которую будет меняться
  private val phonebookIo: Ref[IO, List[Contact]] = Ref.of[IO, List[Contact]](List()).unsafeRunSync()

  // метод, создающий процесс IO для замены ссылки в phonebookIo
  private def updateRefIo(newBook: PhoneBook): IO[Unit] = for {
    _ <- phonebookIo.set(newBook)
  } yield()

  // метод, исполняющий процесс IO для замены ссылки в phonebookIo
  private def updateRef(newBook: PhoneBook): Unit = updateRefIo(newBook).unsafeRunSync()

  // перевод IO-содержимого в иммутабельный лист
  private def getBook(): PhoneBook = phonebookIo.get.unsafeRunSync()




  def addContactIo(c: ContactRequest): IO[Response[IO]] = {
    val book = getBook()
    addContact(book, c) match {
      case Left(InvalidInput) => BadRequest("Invalid input")  // 400
      case Right(x) =>
        updateRef(x)
        Ok(PhoneBookModel(getBook()).asJson)
    }
  }

  def listContactsIo(): IO[Response[IO]] = {
    Ok(PhoneBookModel(getBook()).asJson)
  }

  def findContactByNameIo(name: List[String]): IO[Response[IO]] = {
    val book = getBook()
    findContactsByName(book, name) match {
      case Left(InvalidNameValue) => BadRequest("Invalid name value")  // 400
      case Right(x) => Ok(PhoneBookModel(x).asJson)
    }
  }

  def findContactByPhoneIo(phone: List[String]): IO[Response[IO]] = {
    val book = getBook()
    findContactsByPhone(book, phone) match {
      case Left(InvalidPhoneValue) => BadRequest("Invalid phone value")  // 400
      case Right(x) => Ok(PhoneBookModel(x).asJson)
    }
  }

  def getContactByIdIo(id: Long): IO[Response[IO]] = {
    val book = getBook()
    getContactById(book, id) match {
      case Left(ContactNotFound) => NotFound("Contact not found")  // 404
      case Right(x) => Ok(x.asJson)
    }
  }

  def updateContactIo(id: Long, body: ContactRequest): IO[Response[IO]] = {
    val book = getBook()
    updateContact(book, id, body) match {
      case Left(InvalidInput) => BadRequest("Invalid input")  // 400
      case Right(x) =>
        updateRef(x)
        Ok(PhoneBookModel(getBook()).asJson)
    }
  }

  def deleteContactIo(id: Long): IO[Response[IO]] = {
    val book = getBook()
    deleteContact(book, id) match {
      case Left(InvalidIdSupplied) => BadRequest("Invalid ID supplied")  // 400
      case Left(ContactNotFound) => NotFound("Contact not found")  // 404
      case Right(x) =>
        updateRef(x)
        Ok(PhoneBookModel(getBook()).asJson)
    }
  }
}
