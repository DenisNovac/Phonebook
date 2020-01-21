package phonebook

import org.junit._
import org.junit.Assert._

import io.circe.syntax._
import phonebook.PhoneEntryHandler.PhoneEntry
import phonebook.PhoneEntryHandler.entryDecoder
import java.util.UUID.randomUUID


class PhoneEntrySuit {

  @Test def `Test entry created by "asJson"`: Unit = {
    val name = "Josh"
    val num = "1"
    val entry = PhoneEntry(randomUUID(), name, num).asJson

    val decoded = entryDecoder.decodeJson(entry)

    decoded match {
      case Right(x) => assertEquals((x.name, x.phoneNumber), (name, num))
      case Left(e) => fail(e.toString)
    }
  }


}
