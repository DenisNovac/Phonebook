package phonebook
import org.junit._

import org.junit.Assert._
import phonebook.PhoneBookHandler._


class PhoneBookSuit {

  @Test def `Test empty name is not valid`: Unit = {
    val book = createBook()
    val result = insertEntryToBook(book, "123", "12345")

    result match {
      case Left(InvalidNameFormat) => true
      case _ => fail()
    }
  }

  @Test def `Test empty number is not valid`: Unit = {
    val result = insertEntryToBook(createBook(), "Josh", "")

    result match {
      case Left(InvalidNumberFormat) => true
      case _ => fail()
    }
  }


  @Test def `Test error when non-existing ID from book`: Unit = {
    val result = removeEntryFromBookById(createBook(), "fdsokfsdf")
    result match {
      case Left(NoSuchIdInBookError) => true
      case _ => fail()
    }
  }




}
