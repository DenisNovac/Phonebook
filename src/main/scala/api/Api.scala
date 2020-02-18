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

import cats.Monad
import cats.data.OptionT
import model.ContactModel._
import model.PhoneBookApiModel
import model.PhoneBookModel.PhoneBookModel


class Api[F[_] : Sync : Monad](phoneBookApi: PhoneBookApiModel[F])(implicit contextShift: ContextShift[F])
  extends Http4sDsl[F] {

  /** Декодеры из JSON формата */
  implicit private lazy val requestDecoder: EntityDecoder[F, ContactRequest] = jsonOf[F, ContactRequest]

  /** Энкодеры в JSON формат */
  implicit private lazy val contactsEncoder: EntityEncoder[F, List[Contact]] = jsonEncoderOf[F, List[Contact]]
  implicit private lazy val optionContactEncoder: EntityEncoder[F, Option[Contact]] = jsonEncoderOf[F, Option[Contact]]
  implicit private lazy val rowsChanged: EntityEncoder[F, Int] = jsonEncoderOf[F, Int]


  private val blockingEc = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(4))
  val apiCall: HttpRoutes[F] = HttpRoutes.of[F] {
    case req@GET -> Root / "api" =>
      StaticFile.fromFile(new File("swagger.yaml"), blockingEc, Some(req)).getOrElseF(NotFound())
  }


  val indexCall = HttpRoutes.of[F] {
    case req@GET -> Root =>
      Ok("Phonebook is UP")
  }


  val listContacts = HttpRoutes.of[F] {
    case GET -> Root / "contacts" =>
      Ok(phoneBookApi.listContacts())
  }


  val addContact = HttpRoutes.of[F] {
    case req@POST -> Root / "contact" =>
      val response: F[Int] = for {
        contactRequest <- req.as[ContactRequest]
        r <- phoneBookApi.addContact(contactRequest)
      } yield r
      response >> Ok(response)
  }


  object NameParser extends QueryParamDecoderMatcher[String]("name")  // достаёт имена из куска запроса
  val findContactsByName = HttpRoutes.of[F] {
    case  GET -> Root / "contact" / "findByName" :? NameParser(name) =>
      val response = for {
        r <- phoneBookApi.findContactByName(name.split(",").toList)
      } yield r
      Ok(response)
  }

  object PhoneParser extends QueryParamDecoderMatcher[String]("phone")  // достаёт номера из куска запроса
  val findContactsByPhone = HttpRoutes.of[F] {
    case  GET -> Root / "contact" / "findByPhone" :? PhoneParser(phone) =>
      val response = for {
        r <- phoneBookApi.findContactByPhone(phone.split(",").toList)
      } yield r
      Ok(response)
  }


  val getContactById = HttpRoutes.of[F] {
    case GET -> Root / "contact" / contactId =>
      val response: F[Option[Contact]] = for {
        r <- phoneBookApi.getContactById(contactId.toLong)
      } yield r

      response >> Ok(response)

  }



  private val apiRoutes = indexCall <+> apiCall <+> listContacts <+> addContact <+> findContactsByName <+>
    findContactsByPhone <+> getContactById

  val routes: HttpApp[F] = Router("/" -> apiRoutes).orNotFound



  /*
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
