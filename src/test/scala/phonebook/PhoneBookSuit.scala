package phonebook
import org.junit._

import org.junit.Assert._
import phonebook.PhoneBookHandler._
import java.util.UUID.randomUUID

class PhoneBookSuit {

  @Test def `Test empty name is not valid`: Unit = {
    val result = insertEntryToBook(createBook(), "", "12345")
    assertEquals(Left(InvalidNameFormat), result)
  }

  @Test def `Test empty number is not valid`: Unit = {
    val result = insertEntryToBook(createBook(), "Josh", "")
    assertEquals(Left(InvalidNumberFormat), result)
  }

  @Test def `Test remove non-existing ID from book`: Unit = {
    val result = removeEntryFromBookById(createBook(), "random uuid")
    assertEquals(Left(NoSuchIdInBookError), result)
  }

  @Test def `Test get name by non-existing ID from book`: Unit = {
    val result = getNameById(createBook(), "random uuid")
    assertEquals(Left(NoSuchIdInBookError), result)
  }

  @Test def `Test get phone number by non-existing ID from book`: Unit = {
    val result = getPhoneNumberById(createBook(), "random uuid")
    assertEquals(Left(NoSuchIdInBookError), result)
  }




}
