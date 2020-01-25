package www

import cats.effect.IO
import cats.effect.concurrent.Ref
import io.circe.syntax._
import org.http4s.Response
import org.http4s.dsl.io._
import org.http4s.circe._

import phonebook.ContactHandler._
import phonebook.PhoneBookHandler._

object HttpIoBookHandler {

  // базовая телефонная книга, ссылка на которую будет меняться
  private val phonebookIo: Ref[IO, List[Contact]] = Ref.of[IO, List[Contact]](List()).unsafeRunSync()

  // метод, меняющий ссылку базовой книги
  private def updateRefIo(newBook: PhoneBook): IO[Unit] = for {
    _ <- phonebookIo.set(newBook)
  } yield()

  private def updateRef(newBook: PhoneBook): Unit = updateRefIo(newBook).unsafeRunSync()

  private def getBook(): PhoneBook = phonebookIo.get.unsafeRunSync()

  def addContactIO(c: ContactRequest): IO[Response[IO]] = {
    val book = getBook()
    addContact(book, c) match {
      case Left(x) => BadRequest()  // TODO: Нужен 405 вместо 400
      case Right(x) =>
        updateRef(x)
        Ok(PhoneBookModel(getBook()).asJson)
    }
  }



}
