package model

import ContactModel.{Contact, ContactRequest}
import cats.effect.Async


trait PhoneBookApiModel[F[_]] {

  def addContact(c: ContactRequest): F[Int]

  def listContacts(): F[List[Contact]]

  def findContactByName(name: List[String]): F[List[Contact]]

  def findContactByPhone(phone: List[String]): F[List[Contact]]

  def getContactById(id: Long): F[Option[Contact]]

  def updateContact(id: Long, body: ContactRequest): F[Int]

  def deleteContact(id: Long): F[Int]

}