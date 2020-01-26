package api.handlers

import cats.effect.IO
import org.http4s.Response
import share.ContactModel.ContactRequest

trait ApiPhoneBookHandler {
  def addContactIo(c: ContactRequest): IO[Response[IO]]

  def listContactsIo(): IO[Response[IO]]

  def findContactByNameIo(name: List[String]): IO[Response[IO]]

  def findContactByPhoneIo(phone: List[String]): IO[Response[IO]]

  def getContactByIdIo(id: Long): IO[Response[IO]]

  def updateContactIo(id: Long, body: ContactRequest): IO[Response[IO]]

  def deleteContactIo(id: Long): IO[Response[IO]]
}
