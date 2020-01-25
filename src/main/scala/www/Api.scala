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
        resp <- addContactIO(contactRequest)
      } yield resp
  }


}
