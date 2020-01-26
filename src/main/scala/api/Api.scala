package api

import cats.effect._
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.circe._

import scala.concurrent.ExecutionContext
import java.io.File
import java.util.concurrent._

import share.ContactModel._
import api.handlers.IoCollectionPhoneBookHandler
import api.handlers.IoDbPhoneBookHandler

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

  /** Текущий хендлер */
  //private val handler = IoCollectionPhoneBookHandler
  private val handler = new IoDbPhoneBookHandler




  val indexCall = HttpRoutes.of[IO] {
    case req @ GET -> Root =>
      Ok("Phonebook OK")
  }


  val addContact = HttpRoutes.of[IO] {
    case req @ POST -> Root / "contact" =>
      for {
        contactRequest <- req.as[ContactRequest]
        resp <- handler.addContactIo(contactRequest)
      } yield resp
  }


  val listContacts = HttpRoutes.of[IO] {
    case GET -> Root / "contacts" =>
      for {
        resp <- handler.listContactsIo()
      } yield resp
  }


  object NameParser extends QueryParamDecoderMatcher[String]("name")  // достаёт имена из куска запроса
  val findContactsByName = HttpRoutes.of[IO] {
    case  GET -> Root / "contact" / "findByName" :? NameParser(name) =>
      for {
        resp <- handler.findContactByNameIo(name.split(",").toList)
      } yield resp
  }


  object PhoneParser extends QueryParamDecoderMatcher[String]("phone")  // достаёт номера из куска запроса
  val findContactsByPhone = HttpRoutes.of[IO] {
    case  GET -> Root / "contact" / "findByPhone" :? PhoneParser(phone) =>
      for {
        resp <- handler.findContactByPhoneIo(phone.split(",").toList)
      } yield resp
  }


  val getContactById = HttpRoutes.of[IO] {
    case GET -> Root / "contact" / contactId =>
      for {
        resp <- handler.getContactByIdIo(contactId.toLong)
      } yield resp
  }


  val updateContact = HttpRoutes.of[IO] {
    case req @ PUT -> Root / "contact" / contactId =>
      for {
        contactRequest <- req.as[ContactRequest]
        resp <- handler.updateContactIo(contactId.toLong, contactRequest)
      } yield resp
  }


  val deleteContact = HttpRoutes.of[IO] {
    case DELETE -> Root / "contact" / contactId =>
      for {
        resp <- handler.deleteContactIo(contactId.toLong)
      } yield resp
  }

}
