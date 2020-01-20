package phonebook
import PhoneEntryHandler.PhoneEntry
import java.util.UUID.randomUUID

import com.sun.security.auth.UnixNumericUserPrincipal
import io.circe.Json
import io.circe.syntax._
import scala.util.matching.Regex

object PhoneBookHandler {
  /** Основной хендлер для книг номеров. Работает с иммутабельными книгами.
    *
    */
  type PhoneBook = List[PhoneEntry]

  def createBook(): PhoneBook = Nil

  def bookToJson(book: PhoneBook): Json = book.asJson

  def insertEntryToBook(book: PhoneBook, name: String, number: String): PhoneBook =
    PhoneEntry(randomUUID(),name, number) :: book

  def removeEntryFromBook(book: PhoneBook, uuid: String): PhoneBook =
    book.filterNot(_.id.toString == uuid)


  def findByPartialUuid(book: PhoneBook, uuid: String): Option[PhoneEntry] = {
    val filter = book.filter(_.id.toString.startsWith(uuid))
    filter match {
      case Nil => None
      case x :: Nil => Some(x)
      case x :: xs => None
    }
  }


}
