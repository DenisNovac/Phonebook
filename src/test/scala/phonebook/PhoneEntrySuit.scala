package phonebook

import org.junit._
import org.junit.Assert.assertEquals
import io.circe.syntax._
import io.circe.Json

import phonebook.PhoneEntryHandler.PhoneEntry

import java.util.UUID.randomUUID
import java.util.UUID.fromString

class PhoneEntrySuit {

  @Test def `Test entry created by "asJson"`: Unit = {
    val uuid = fromString("7affcd26-6181-49fd-9c3b-10166b4bdfbe")

    val sampleEntry =
      s"""{
        |  "id" : "$uuid",
        |  "name" : "Josh",
        |  "phoneNumber" : "1"
        |}""".stripMargin

    val entry = PhoneEntry(uuid, "Josh", "1").asJson
    assertEquals(sampleEntry, entry.toString)
  }


  @Test def `Test list of entries created by "asJson"`: Unit = {
    val uuid1 = fromString("7affcd26-6181-49fd-9c3b-10166b4bdfbe")
    val uuid2 = fromString("b74b907b-a742-4eee-a893-af5991fb1dff")
    val sampleList =
      s"""[
        |  {
        |    "id" : "$uuid1",
        |    "name" : "Josh",
        |    "phoneNumber" : "1"
        |  },
        |  {
        |    "id" : "$uuid2",
        |    "name" : "Sam",
        |    "phoneNumber" : "2"
        |  }
        |]""".stripMargin


    val entry1 = PhoneEntry(uuid1, "Josh", "1")
    val entry2 = PhoneEntry(uuid2, "Sam", "2")
    val book = List[PhoneEntry](entry1, entry2)

    assertEquals(sampleList, book.asJson.toString)
  }
}
