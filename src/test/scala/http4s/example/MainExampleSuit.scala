package http4s.example

import cats.effect._
import io.circe.Json
import org.junit.Test
import org.junit.Assert._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.implicits._
import phonebook.ContactHandler._

class MainExampleSuit {

  @Test def `Test index service must return Ok`: Unit = {
    val request = Request[IO](GET, uri"/")
    val io = MainExample.indexService.orNotFound.run(request)
    val response = io.unsafeRunSync
    assertEquals(Ok, response.status)
  }

  @Test def `Hello service must return valid name`: Unit = {
    val request = Request[IO](GET, uri"/hello?name=denis")
    val io = MainExample.helloWorldService.orNotFound.run(request)
    val response = io.flatMap(_.as[String]).unsafeRunSync

    assertEquals("Hello, denis", response)
  }

  @Test def `Get json gives valid contact`: Unit = {
    val request = Request[IO](GET, uri"/contact")
    val io: IO[Response[IO]] = MainExample.getJsonWithContact.orNotFound.run(request)
    val response = io.flatMap(_.as[Json]).unsafeRunSync()

    val contact = contactEncoder(Contact(1, "Denis", "123"))
    assertEquals(contact, response)
  }



}
