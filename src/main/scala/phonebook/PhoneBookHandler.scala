package phonebook
import ContactHandler.{Contact, ContactRequest}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, DecodingFailure, Encoder, Json}
import io.circe.syntax._

object PhoneBookHandler {

  type PhoneBook = List[Contact]

  /** Модель JSON для телефонной книги. Рядом находятся энкодер и декодер для
   *  преобразования в JSON или обратно
   *  @param items телефонная книга, которую требуется вывести в JSON
   */
  case class PhoneBookModel(items: PhoneBook)
  implicit val bookEncoder: Encoder[PhoneBookModel] = deriveEncoder[PhoneBookModel]
  implicit val bookDecoder: Decoder[PhoneBookModel] = deriveDecoder[PhoneBookModel]

  def listContacts(book: PhoneBook): Json = PhoneBookModel(book).asJson

  def jsonToBook(json: Json): Either[DecodingFailure,PhoneBook] = bookDecoder.decodeJson(json) match {
    case Right(PhoneBookModel(items)) => Right(items)
    case Left(e) => Left(e)
  }

  def createBook(): PhoneBook = Nil

  def isNameValid(name: String): Boolean =
    !name.isBlank

  def isNumberValid(number: String): Boolean =
    !number.isBlank


  def addContact(book: PhoneBook, contact: ContactRequest): Either[PhoneBookError, PhoneBook] =
    if (isNameValid(contact.name) & isNumberValid(contact.phoneNumber))
      Right(Contact(IdGenerator.next(), contact.name, contact.phoneNumber) :: book)
    else Left(InvalidInput)


  def contactId(book: PhoneBook, id: Long): Either[PhoneBookError, Contact] =
    book.filter(_.id equals id) match {
      case Nil => Left(ContactNotFound)
      case x :: Nil => Right(x)
    }


  def findContactsByName(book: PhoneBook, name: String): Either[PhoneBookError, PhoneBook] =
    if (isNameValid(name))
      Right(book.filter(_.name contains name))
    else Left(InvalidNameValue)


  def findContactsByPhone(book: PhoneBook, phone: String): Either[PhoneBookError, PhoneBook] =
    if (isNumberValid(phone))
      Right(book.filter(_.phoneNumber contains phone))
    else Left(InvalidPhoneValue)


  def updateContact(book: PhoneBook, id: Long, input: ContactRequest): Either[PhoneBookError, PhoneBook] = {
    book.find(_.id equals id) match {
      case Some(x) if isNameValid(input.name) & isNumberValid(input.phoneNumber) =>
        Right(Contact(id, input.name, input.phoneNumber) :: book.filterNot(_.id equals id))
      case None => Left(InvalidInput)
      case _ => Left(InvalidInput)
    }
  }


  def deleteContact(book: PhoneBook, id: Long): Either[PhoneBookError, PhoneBook] = {
    book.filter(_.id equals id) match {
      case x :: Nil => Right(book.filterNot(_.id == id))
      case x :: xs => Left(InvalidIdSupplied)
      case Nil => Left(ContactNotFound)
    }
  }
}


