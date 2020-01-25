package www

import cats.effect._

import org.http4s.HttpRoutes
import org.http4s.dsl.io._
import org.http4s.circe._

import phonebook.ContactHandler._
import HttpIoBookHandler._


object Api {
  implicit val requestDecoder = jsonOf[IO, ContactRequest]


  val indexCall = HttpRoutes.of[IO] {
    case GET -> Root =>
      Ok("Phonebook OK")
  }

  // Добавить телефон и имя в справочник
  val addContact = HttpRoutes.of[IO] {
    case req @ POST -> Root / "contact" =>
      for {
        contactRequest <- req.as[ContactRequest]
        resp <- addContactIo(contactRequest)
      } yield resp
  }

  val listContacts = HttpRoutes.of[IO] {
    case GET -> Root / "contacts" =>
      for {
        resp <- listContactsIo()
      } yield resp
  }

  object NameParser extends QueryParamDecoderMatcher[String]("name")  // достаёт имена из куска запроса
  val findContactsByName = HttpRoutes.of[IO] {
    case  GET -> Root / "contact" / "findByName" :? NameParser(name) =>
      for {
        resp <- findContactByNameIo(name.split(",").toList)
      } yield resp
  }

  object PhoneParser extends QueryParamDecoderMatcher[String]("phone")  // достаёт номера из куска запроса
  val findContactsByPhone = HttpRoutes.of[IO] {
    case  GET -> Root / "contact" / "findByPhone" :? PhoneParser(phone) =>
      for {
        resp <- findContactByPhoneIo(phone.split(",").toList)
      } yield resp
  }

  val getContactById = HttpRoutes.of[IO] {
    case GET -> Root / "contact" / contactId =>
      for {
        resp <- getContactByIdIo(contactId.toLong)
      } yield resp
  }

  val updateContact = HttpRoutes.of[IO] {
    case req @ PUT -> Root / "contact" / contactId =>
      for {
        contactRequest <- req.as[ContactRequest]
        resp <- updateContactIo(contactId.toLong, contactRequest)
      } yield resp
  }

  val deleteContact = HttpRoutes.of[IO] {
    case DELETE -> Root / "contact" / contactId =>
      for {
        resp <- deleteContactIo(contactId.toLong)
      } yield resp
  }

}
