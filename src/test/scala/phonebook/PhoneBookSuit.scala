package phonebook
import org.junit._

import org.junit.Assert._
import phonebook.PhoneBookHandler._
import phonebook.PhoneEntryHandler._

class PhoneBookSuit {

  @Test def `Test empty name is not valid`: Unit = {
    val result = insertEntryToBook(createBook(), "", "12345")
    assertEquals(Left(InvalidNameFormat), result)
  }

  @Test def `Test empty number is not valid`: Unit = {
    val result = insertEntryToBook(createBook(), "Josh", "")
    assertEquals(Left(InvalidNumberFormat), result)
  }

  @Test def `Inserting in book works`: Unit = {
    val name = "James Sunderland"
    val number = "883-223"
    val result = insertEntryToBook(createBook(), name, number)

    result match {
      case Right(PhoneEntry(_, n, nn) :: Nil) => assertEquals((n, nn), (name, number))
      case Right(x) => fail(x.toString)
      case Left(e) => fail(e.toString)
    }
  }

  @Test def `Book to json and backwards works`: Unit = {
    val bookOne = insertEntryToBook(createBook(), "Harry Mason", "821-223") match { case Right(x) => x }
    val bookTwo = insertEntryToBook(bookOne, "James Sunderland", "324-223") match { case Right(x) => x }

    val json = bookToJson(bookTwo)
    val decoded = jsonToBook(json)

    assertEquals(Right(bookTwo), decoded)
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
