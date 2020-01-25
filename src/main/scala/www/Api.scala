package www

import cats.effect._
import cats.effect.concurrent.Ref
import cats.implicits._
import fs2.concurrent.Signal
import org.http4s.HttpRoutes
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.server.blaze._
import org.http4s.server.Router
import org.http4s.circe._
import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import phonebook.ContactHandler._
import phonebook.PhoneBookHandler._

import scala.concurrent.ExecutionContext.Implicits.global


object Api {
  implicit val requestDecoder = jsonOf[IO, ContactRequest]
  implicit val contactDecoder = jsonOf[IO, Contact]

  val phonebook: Ref[IO, List[Contact]] = Ref.of[IO, List[Contact]](List()).unsafeRunSync()

  def updateBook(r: Ref[IO, List[Contact]], c: Contact) = for {
    _ <- r.update(c :: _)
  } yield ()




  val indexCall = HttpRoutes.of[IO] {
    case GET -> Root =>
      Ok("Phonebook OK")
  }

  // Добавить телефон и имя в справочник
  val addContact = HttpRoutes.of[IO] {
    case req @ POST -> Root / "contact" =>
      for {
        user <- req.as[ContactRequest]
        _ <- updateBook(phonebook, Contact(1, user.name, user.phoneNumber))
        resp <- Ok(phonebook.get.unsafeRunSync.asJson)
        //resp <- Ok(Contact(1, user.name, user.phoneNumber).asJson)
      } yield (resp)
  }


}
