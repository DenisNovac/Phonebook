package phonebook
import PhoneEntryHandler.PhoneEntry
import java.util.UUID.randomUUID
import java.util.UUID.fromString

import com.sun.security.auth.UnixNumericUserPrincipal
import io.circe.Json
import io.circe.syntax._
import scala.util.matching.Regex

object PhoneBookHandler {
  /** Основной хендлер для телефонных книг. Работает с иммутабельными книгами.
    *
    */
  type PhoneBook = List[PhoneEntry]


  def createBook(): PhoneBook = Nil


  def bookToJson(book: PhoneBook): Json = book.asJson


  def insertEntryToBook(book: PhoneBook, name: String, number: String): Either[PhoneBookError, PhoneBook] =
    if (name.isBlank) Left(InvalidNameFormat)
    else if (number.isBlank) Left(InvalidNumberFormat)
    else Right(PhoneEntry(randomUUID(),name, number) :: book)


  def getNameById(book: PhoneBook, uuid: String): Either[PhoneBookError, String] =
    book.filter(_.id.toString equals uuid) match {
      case Nil => Left(NoSuchIdInBookError)
      case x :: Nil => Right(x.name)
    }


  def getPhoneNumberById(book: PhoneBook, uuid: String): Either[PhoneBookError, String] =
    book.filter(_.id.toString equals uuid) match {
      case Nil => Left(NoSuchIdInBookError)
      case x :: Nil => Right(x.phoneNumber)
    }


  def getEntriesByPartialName(book: PhoneBook, name: String): PhoneBook =
    book.filter(_.name contains name)


  def getEntriesByPartialNumber(book: PhoneBook, number: String): PhoneBook =
    book.filter(_.phoneNumber contains number)


  def changeNameById(book: PhoneBook, uuid: String, newName: String): Either[PhoneBookError, PhoneBook] =
    book.filter(_.id.toString equals uuid) match {
      case Nil => Left(NoSuchIdInBookError)
      case x :: Nil =>
        Right(PhoneEntry(fromString(uuid), newName, x.phoneNumber) :: book.filterNot(_.id.toString equals uuid))
    }


  def changePhoneNumberById(book: PhoneBook, uuid: String, newNumber: String): Either[PhoneBookError, PhoneBook] =
    book.filter(_.id.toString equals uuid) match {
      case Nil => Left(NoSuchIdInBookError)
      case x :: Nil =>
        Right(PhoneEntry(fromString(uuid), x.name, newNumber) :: book.filterNot(_.id.toString equals uuid))
    }


  def removeEntryFromBookById(book: PhoneBook, uuid: String): Either[PhoneBookError, PhoneBook] = {
    book.find(_.id.toString equals uuid) match {
      case None => Left(NoSuchIdInBookError)
      case Some(x) => Right(book.filterNot(_.id.toString == uuid))
    }
  }


}


