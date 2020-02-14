package model

import share.ContactModel.Contact
import share.PhoneBookModel.PhoneBook
import cats._
import cats.data._
import cats.effect._
import cats.effect.IO
import cats.effect.implicits._

import doobie._
import doobie.implicits._
import doobie.util.compat._
import doobie.postgres.implicits._
import doobie.util.ExecutionContexts

import cats.implicits._


trait PhoneBookModel[F[_]] {

  def create(contact: Contact): F[Unit]

  def read(id: Long): F[Contact]

  def update(contact: Contact): F[Contact]

  def delete(id: Long): F[Contact]

  def list: F[List[Contact]]

}




class PhoneBookDoobieModel extends PhoneBookModel[ConnectionIO] {

  def create(contact: Contact): ConnectionIO[Unit] =
    sql"INSERT INTO phonebook (name, phoneNumber) VALUES (${contact.name}, ${contact.phoneNumber})"
      .update
      .run
      .void

  def read(id: Long): ConnectionIO[Contact] = ???

  def update(contact: Contact): ConnectionIO[Contact] = ???

  def delete(id: Long): ConnectionIO[Contact] = ???

  def list: ConnectionIO[List[Contact]] = ???


}

class CreateContactRequest(name: String, phoneNumber: String) {

  def toContac5at = Contact(1, name, phoneNumber)
}





class FooRepo(phoneBookDoobieModel: PhoneBookDoobieModel) {

  def fooBar(size: Long): ConnectionIO[String] = size.toString.pure[ConnectionIO]

  def baz(contactId: Long): ConnectionIO[String]  = phoneBookDoobieModel.read(contactId).flatMap{
     contact => fooBar(contact.id)
  }

}


trait FooServiceTrait[F[_]] {

  def heloWord(id: Long): F[Unit]

}

class FooService(fooRepo: FooRepo, transactor: Transactor[IO]) extends FooServiceTrait[IO] {

  def heloWord(id: Long): IO[Unit]  = fooRepo.baz(id).transact(transactor).map{
    println(_)
  }


}


trait PhoneBookApi[F[_]] {

  def create(contact: CreateContactRequest): F[Unit]

  def read(id: Long): F[ContactResponse]

  def update(id: Long, contact: CreateContactRequest): F[ContactResponse]

  def delete(id: Long): F[ContactResponse]

  def list: F[List[ContactResponse]]

}