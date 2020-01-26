package api
import cats.effect._
import io.circe.Json
import org.junit.Test
import org.junit.Assert._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.implicits._
import api.handlers._
import share.ContactModel._
import org.http4s.circe._
import io.circe.syntax._
import org.http4s.headers.`Content-Type`
import share.PhoneBookModel.PhoneBookModel
import collection.IdGenerator


class ApiSuit {

  /** Апи для тестирования в каждом тесте новое со сбросом счётчика/базы данных*/
  def api() = {

    IdGenerator.reset()
    val handler = new IoCollectionPhoneBookHandler()

    /*val handler = new IoDbPhoneBookHandler("localhost", "5432", "postgres", "postgres", "P@ssw0rd")
    handler.resetTable()*/

    new Api(handler)
  }


  @Test def `Test index service must return Ok`: Unit = {
    val request = Request[IO](GET, uri"/")
    val io = api().indexCall.orNotFound.run(request)
    val response = io.unsafeRunSync
    assertEquals(Ok, response.status)
  }

  @Test def `Get 404 on invalid request`: Unit = {
    val request = Request[IO](GET, uri"/help_me_please")
    val io = api().indexCall.orNotFound.run(request)
    val response = io.unsafeRunSync
    assertEquals(NotFound, response.status)
  }


  @Test def `Get added contact`: Unit = {
    val req = ContactRequest("1", "2").asJson
    val exp = PhoneBookModel(List(Contact(1,"1","2"))).asJson

    val request = Request[IO](POST, uri"/contact").withEntity(req)
    val io = api().addContact.orNotFound.run(request)
    val response = io.flatMap(_.as[Json]).unsafeRunSync()
    assertEquals(exp, response)
  }

  @Test def `Get added contact from listContacts`: Unit = {
    val req = ContactRequest("1", "2").asJson
    val exp = PhoneBookModel(List(Contact(1,"1","2"))).asJson

    val persist = api()

    val request = Request[IO](POST, uri"/contact").withEntity(req)
    persist.addContact.orNotFound.run(request).unsafeRunSync()

    val request2 = Request[IO](GET, uri"/contacts")
    val io2 = persist.listContacts.orNotFound.run(request2)
    val response2 = io2.flatMap(_.as[Json]).unsafeRunSync()

    assertEquals(exp, response2)
  }

  @Test def `Find contact from findName`: Unit = {
    val req = ContactRequest("John", "123").asJson
    val exp = PhoneBookModel(List(Contact(1, "John", "123"))).asJson

    val persist = api()

    val request = Request[IO](POST, uri"/contact").withEntity(req)
    persist.addContact.orNotFound.run(request).unsafeRunSync()

    val request2 = Request[IO](GET, uri"/contact/findByName?name=Jo")
    val io2 = persist.findContactsByName.orNotFound.run(request2)
    val response2 = io2.flatMap(_.as[Json]).unsafeRunSync()

    assertEquals(exp, response2)
  }


  @Test def `Find two contacts from findName`: Unit = {
    val exp = PhoneBookModel(List(Contact(2, "Josh", "432432"), Contact(1, "John", "123"))).asJson

    val persist = api()

    val request = Request[IO](POST, uri"/contact").withEntity(ContactRequest("John", "123").asJson)
    persist.addContact.orNotFound.run(request).unsafeRunSync()

    val request2 = Request[IO](POST, uri"/contact").withEntity(ContactRequest("Josh", "432432").asJson)
    persist.addContact.orNotFound.run(request2).unsafeRunSync()


    val request3 = Request[IO](GET, uri"/contact/findByName?name=Jo")
    val io3 = persist.findContactsByName.orNotFound.run(request3)
    val response3 = io3.flatMap(_.as[Json]).unsafeRunSync()

    assertEquals(exp, response3)
  }




  @Test def `Find contact from findPhone`: Unit = {
    val req = ContactRequest("John", "123").asJson
    val exp = PhoneBookModel(List(Contact(1, "John", "123"))).asJson

    val persist = api()

    val request = Request[IO](POST, uri"/contact").withEntity(req)
    persist.addContact.orNotFound.run(request).unsafeRunSync()

    val request2 = Request[IO](GET, uri"/contact/findByPhone?phone=12")
    val io2 = persist.findContactsByPhone.orNotFound.run(request2)
    val response2 = io2.flatMap(_.as[Json]).unsafeRunSync()

    assertEquals(exp, response2)
  }

  @Test def `Find two contacts from findPhone`: Unit = {
    val exp = PhoneBookModel(List(Contact(2, "Josh", "122432"), Contact(1, "John", "123"))).asJson

    val persist = api()

    val request = Request[IO](POST, uri"/contact").withEntity(ContactRequest("John", "123").asJson)
    persist.addContact.orNotFound.run(request).unsafeRunSync()

    val request2 = Request[IO](POST, uri"/contact").withEntity(ContactRequest("Josh", "122432").asJson)
    persist.addContact.orNotFound.run(request2).unsafeRunSync()


    val request3 = Request[IO](GET, uri"/contact/findByPhone?phone=12")
    val io3 = persist.findContactsByPhone.orNotFound.run(request3)
    val response3 = io3.flatMap(_.as[Json]).unsafeRunSync()

    assertEquals(exp, response3)
  }


  @Test def `Find contact from id`: Unit = {
    val req = ContactRequest("John", "123").asJson
    val exp = Contact(1, "John", "123").asJson

    val persist = api()

    val request = Request[IO](POST, uri"/contact").withEntity(req)
    persist.addContact.orNotFound.run(request).unsafeRunSync()

    val request2 = Request[IO](GET, uri"/contact/1")
    val io2 = persist.getContactById.orNotFound.run(request2)
    val response2 = io2.flatMap(_.as[Json]).unsafeRunSync()

    assertEquals(exp, response2)
  }


  @Test def `Update contact by id`: Unit = {
    val req = ContactRequest("John", "123").asJson
    val exp = PhoneBookModel(List(Contact(1, "Johnny", "321"))).asJson

    val persist = api()

    val request = Request[IO](POST, uri"/contact").withEntity(req)
    persist.addContact.orNotFound.run(request).unsafeRunSync()

    val new_req = ContactRequest("Johnny", "321").asJson
    val request2 = Request[IO](PUT, uri"/contact/1").withEntity(new_req)
    persist.updateContact.orNotFound.run(request2).unsafeRunSync()


    val request3 = Request[IO](GET, uri"/contacts")
    val io3 = persist.listContacts.orNotFound.run(request3)
    val response3 = io3.flatMap(_.as[Json]).unsafeRunSync()
    assertEquals(exp, response3)
  }

  @Test def `Delete contact by id`: Unit = {
    val req = ContactRequest("John", "123").asJson
    val exp = PhoneBookModel(List()).asJson

    val persist = api()

    val request = Request[IO](POST, uri"/contact").withEntity(req)
    persist.addContact.orNotFound.run(request).unsafeRunSync()

    val request2 = Request[IO](DELETE, uri"/contact/1")
    persist.deleteContact.orNotFound.run(request2).unsafeRunSync()

    val request3 = Request[IO](GET, uri"/contacts")
    val io3 = persist.listContacts.orNotFound.run(request3)
    val response3 = io3.flatMap(_.as[Json]).unsafeRunSync()
    assertEquals(exp, response3)
  }

}
