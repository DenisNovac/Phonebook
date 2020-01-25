package www

import cats.effect._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.circe._

import scala.concurrent.ExecutionContext
import java.io.File
import java.util.concurrent._

import phonebook.ContactHandler._
import HttpIoBookHandler._
import org.log4s._


object Api {
  /** Декодер реквестов в JSON формата ContactRequest */
  implicit lazy val requestDecoder = jsonOf[IO, ContactRequest]

  /** Эти имплиситы необходимы для возврата файла с описанием API по запросу */
  val blockingEc = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(4))
  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContext.global)

  val apiCall = HttpRoutes.of[IO] {
    case req @ GET -> Root / "api" =>
      StaticFile.fromFile(new File("swagger.yaml"), blockingEc, Some(req)).getOrElseF(NotFound())
  }




  val indexCall = HttpRoutes.of[IO] {
    case req @ GET -> Root =>
      Ok("Phonebook OK")
  }


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
