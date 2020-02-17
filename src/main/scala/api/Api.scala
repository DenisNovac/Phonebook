package api

import cats.effect._
import cats.implicits._

import org.http4s._
import org.http4s.circe._
import org.http4s.implicits._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.http4s.HttpRoutes

import org.log4s.getLogger


import scala.concurrent.ExecutionContext
import java.io.File
import java.util.concurrent._

import model.ContactModel._
import model.PhoneBookApiModel


class Api[F[_]: Async] (phoneBookApi: PhoneBookApiModel[F]) (implicit contextShift: ContextShift[F])
  extends Http4sDsl[F] {

  /** Декодеры из JSON формата */
  implicit private lazy val requestDecoder: EntityDecoder[F, ContactRequest] = jsonOf[F, ContactRequest]

  /** Энкодеры в JSON формат */
  implicit private lazy val contactsEncoder: EntityEncoder[F, List[Contact]] = jsonEncoderOf[F, List[Contact]]
  implicit private lazy val rowsChanged: EntityEncoder[F, Int] = jsonEncoderOf[F, Int]


  private val blockingEc = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(4))
  val apiCall: HttpRoutes[F] = HttpRoutes.of[F] {
    case req @ GET -> Root / "api" =>
      StaticFile.fromFile(new File("swagger.yaml"), blockingEc, Some(req)).getOrElseF(NotFound())
  }


  val indexCall = HttpRoutes.of[F] {
    case req @ GET -> Root =>
      Ok("Phonebook is UP")
  }


  val listContacts = HttpRoutes.of[F] {
    case GET -> Root / "contacts" =>
      Ok(phoneBookApi.listContacts())
  }


  val addContact = HttpRoutes.of[F] {
    case req @ POST -> Root / "contact" => {

      for {
        contactRequest <- req.as[ContactRequest]
        resp <- phoneBookApi.addContact(contactRequest)
      } yield ()

      Ok(phoneBookApi.listContacts())
    }
  }


  private val apiRoutes = indexCall <+> apiCall <+> listContacts <+> addContact
  val routes: HttpApp[F] = Router("/" -> apiRoutes).orNotFound


/*
  val addContact = HttpRoutes.of[F] {
    case req @ POST -> Root / "contact" =>
      for {
        contactRequest <- req.as[ContactRequest]
        resp <- phoneBookApi.addContact(contactRequest)
      } yield {
        resp match {
          case i>0 => Ok()
          case _ => InternalServerError()
        }
      }
  }
*/



  /*val listContacts = HttpRoutes.of[IO] {
    case GET -> Root / "contacts" =>
      phoneBookApi.listContacts().unsafeRunSync() match {
        case x::xs => Ok(PhoneBookModel(x::xs).asJson)
        case _ => InternalServerError()
      }
  }*/

/*
  object NameParser extends QueryParamDecoderMatcher[String]("name")  // достаёт имена из куска запроса
  val findContactsByName = HttpRoutes.of[IO] {
    case  GET -> Root / "contact" / "findByName" :? NameParser(name) =>
      for {
        resp <- phoneBookApi.findContactByName(name.split(",").toList)
      } yield {
        Ok(PhoneBookModel(resp).asJson)
      }.unsafeRunSync()
  }


  object PhoneParser extends QueryParamDecoderMatcher[String]("phone")  // достаёт номера из куска запроса
  val findContactsByPhone = HttpRoutes.of[IO] {
    case  GET -> Root / "contact" / "findByPhone" :? PhoneParser(phone) =>
      for {
        resp <- phoneBookApi.findContactByPhone(phone.split(",").toList)
      } yield {
        Ok(PhoneBookModel(resp).asJson)
      }.unsafeRunSync()
  }


  val getContactById = HttpRoutes.of[IO] {
    case GET -> Root / "contact" / contactId =>
      for {
        resp <- phoneBookApi.getContactById(contactId.toLong)
      } yield {
        resp match {
          case x => Ok(x.asJson)
          case _ => InternalServerError()
        }
      }.unsafeRunSync()
  }


  val updateContact = HttpRoutes.of[IO] {
    case req @ PUT -> Root / "contact" / contactId =>
      for {
        contactRequest <- req.as[ContactRequest]
        resp <- phoneBookApi.updateContact(contactId.toLong, contactRequest)
      } yield {
        resp match {
          case i>0 => Ok()
          case _ => InternalServerError()
        }
      }.unsafeRunSync()
  }


  val deleteContact = HttpRoutes.of[IO] {
    case DELETE -> Root / "contact" / contactId =>
      for {
        resp <- phoneBookApi.deleteContact(contactId.toLong)
      } yield {
        resp match {
          case i>0 => Ok()
          case _ => InternalServerError()
        }
      }.unsafeRunSync()
  }*/
}
