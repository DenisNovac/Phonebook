package phonebook

import org.junit._
import io.circe.syntax._
import phonebook.PhoneEntryHandler.PhoneEntry
import phonebook.PhoneEntryHandler.decoder
import java.util.UUID.randomUUID

class PhoneEntrySuit {

  @Test def `Test entry created by "asJson"`: Unit = {
    val Name = "Josh"
    val Num = "1"
    val entry = PhoneEntry(randomUUID(), Name, Num).asJson

    val decoded = decoder.decodeJson(entry)

    decoded match {
      case Right(x) => x match { case PhoneEntry(_, Name, Num) => true }
      case Left(e) => false
    }
  }

}
