package collection
import share.ContactModel.{Contact, ContactRequest}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, DecodingFailure, Encoder, Json}
import io.circe.syntax._
import share.PhoneBookModel.{PhoneBook, PhoneBookModel}
import share.PhoneBookModel._
import share.InputValidator._
import share.{ContactNotFound, InvalidIdSupplied, InvalidInput, InvalidNameValue, InvalidPhoneValue, PhoneBookError, UnknownError}

object CollectionPhoneBookHandler {

  def listContacts(book: PhoneBook): Json = PhoneBookModel(book).asJson

  def jsonToBook(json: Json): Either[DecodingFailure,PhoneBook] = bookDecoder.decodeJson(json) match {
    case Right(PhoneBookModel(items)) => Right(items)
    case Left(e) => Left(e)
  }

  def createBook(): PhoneBook = Nil

  def addContact(book: PhoneBook, body: ContactRequest): Either[PhoneBookError, PhoneBook] =
    if (isNameValid(body.name) & isPhoneValid(body.phoneNumber))
      Right(Contact(IdGenerator.next(), body.name, body.phoneNumber) :: book)
    else Left(InvalidInput)


  def getContactById(book: PhoneBook, contactId: Long): Either[PhoneBookError, Contact] =
    book.filter(_.id equals contactId) match {
      case Nil => Left(ContactNotFound)
      case x :: Nil => Right(x)
      case x :: xs => Left(UnknownError)
    }


  def findContactsByName(book: PhoneBook, name: List[String]): Either[PhoneBookError, PhoneBook] = {
    if (name forall(n => isNameValid(n))) {
      val search = for {
        n <- name
        x = book.filter(_.name startsWith n)
        if x.nonEmpty
      } yield x

      Right(search.flatten)
    }
    else Left(InvalidNameValue)
  }

  def findContactsByPhone(book: PhoneBook, phone: List[String]): Either[PhoneBookError, PhoneBook] = {
    if (phone forall(p => isPhoneValid(p))) {
      val search = for {
        p <- phone
        x = book.filter(_.phoneNumber startsWith p)
        if x.nonEmpty
      } yield x

      Right(search.flatten)
    }
    else Left(InvalidPhoneValue)
  }

  def updateContact(book: PhoneBook, contactId: Long, body: ContactRequest): Either[PhoneBookError, PhoneBook] = {
    book.find(_.id equals contactId) match {
      case Some(x) if isNameValid(body.name) & isPhoneValid(body.phoneNumber) =>
        Right(Contact(contactId, body.name, body.phoneNumber) :: book.filterNot(_.id equals contactId))
      case None => Left(InvalidInput)
      case _ => Left(InvalidInput)
    }
  }

  def deleteContact(book: PhoneBook, contactId: Long): Either[PhoneBookError, PhoneBook] = {
    book.filter(_.id equals contactId) match {
      case x :: Nil => Right(book.filterNot(_.id == contactId))
      case x :: xs => Left(InvalidIdSupplied)
      case Nil => Left(ContactNotFound)
    }
  }
}


