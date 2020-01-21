package phonebook
import PhoneEntryHandler.PhoneEntry
import java.util.UUID.randomUUID
import java.util.UUID.fromString

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, DecodingFailure, Encoder, Json}
import io.circe.syntax._

object PhoneBookHandler {

  type PhoneBook = List[PhoneEntry]

  /** Модель JSON для телефонной книги. Рядом находятся энкодер и декодер для
   *  преобразования в JSON или обратно
   *  @param items телефонная книга, которую требуется вывести в JSON
   */
  case class PhoneBookModel(items: PhoneBook)
  implicit val bookEncoder: Encoder[PhoneBookModel] = deriveEncoder[PhoneBookModel]
  implicit val bookDecoder: Decoder[PhoneBookModel] = deriveDecoder[PhoneBookModel]

  def bookToJson(book: PhoneBook): Json = PhoneBookModel(book).asJson

  def jsonToBook(json: Json): Either[DecodingFailure,PhoneBook] = bookDecoder.decodeJson(json) match {
    case Right(PhoneBookModel(items)) => Right(items)
    case Left(e) => Left(e)
  }


  def createBook(): PhoneBook = Nil

  //def jsonToBook(json: Json): PhoneBook = decoder.decodeJson(json)

  def insertEntryToBook(book: PhoneBook, name: String, number: String): Either[PhoneBookError, PhoneBook] =
    if (name.isBlank) Left(InvalidNameFormat)
    else if (number.isBlank) Left(InvalidNumberFormat)
    else Right(PhoneEntry(randomUUID(),name, number) :: book)


  def getNameById(book: PhoneBook, uuid: String): Either[PhoneBookError, String] =
    book.filter(_.id.toString equals uuid) match {
      case Nil => Left(NoSuchIdInBookError)
      case x :: Nil => Right(x.name)
      case x :: xs => Left(MoreThanOneIdError)
    }


  def getPhoneNumberById(book: PhoneBook, uuid: String): Either[PhoneBookError, String] =
    book.filter(_.id.toString equals uuid) match {
      case Nil => Left(NoSuchIdInBookError)
      case x :: Nil => Right(x.phoneNumber)
      case x :: xs => Left(MoreThanOneIdError)
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
      case x :: xs => Left(MoreThanOneIdError)
    }


  def changePhoneNumberById(book: PhoneBook, uuid: String, newNumber: String): Either[PhoneBookError, PhoneBook] =
    book.filter(_.id.toString equals uuid) match {
      case Nil => Left(NoSuchIdInBookError)
      case x :: Nil =>
        Right(PhoneEntry(fromString(uuid), x.name, newNumber) :: book.filterNot(_.id.toString equals uuid))
      case x :: xs => Left(MoreThanOneIdError)
    }


  def removeEntryFromBookById(book: PhoneBook, uuid: String): Either[PhoneBookError, PhoneBook] = {
    book.find(_.id.toString equals uuid) match {
      case None => Left(NoSuchIdInBookError)
      case Some(x) => Right(book.filterNot(_.id.toString == uuid))
    }
  }


}


