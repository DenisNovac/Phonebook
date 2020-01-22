package phonebook

import org.junit._
import org.junit.Assert._

import io.circe.syntax._
import phonebook.ContactHandler.Contact
import phonebook.ContactHandler.contactDecoder
import java.util.UUID.randomUUID


class ContactSuit {

  @Test def `Test entry created by "asJson"`: Unit = {
    val name = "Josh"
    val num = "1"
    val entry = Contact(1, name, num).asJson

    val decoded = contactDecoder.decodeJson(entry)

    decoded match {
      case Right(x) => assertEquals((x.name, x.phoneNumber), (name, num))
      case Left(e) => fail(e.toString)
    }
  }


}
