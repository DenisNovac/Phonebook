package phonebook
import org.junit._

import org.junit.Assert._
import phonebook.PhoneBookHandler._
import phonebook.PhoneEntryHandler._
import java.util.UUID._

class PhoneBookSuit {



  @Test def `Empty name is not valid`: Unit = {
    assertFalse(isNameValid(""))
  }

  @Test def `Empty number is not valid`: Unit = {
    assertFalse(isNumberValid(""))
  }

  @Test def `Insert empty name is not possible`: Unit = {
    val result = insertEntryToBook(createBook(), "", "12345")
    assertEquals(Left(InvalidNameFormat), result)
  }

  @Test def `Insert empty number is not possible`: Unit = {
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
    val book = PhoneEntry(1, "Harry Mason", "821-223") ::
      PhoneEntry(2, "James Sunderland", "324-223") :: Nil
    val json = bookToJson(book)
    val decoded = jsonToBook(json)
    assertEquals(Right(book), decoded)
  }



  @Test def `Get one entry by partial name`: Unit = {
    val book = PhoneEntry(1, "Gwyn. Lord of Cinder", "1234342") :: Nil
    val entry = getEntriesByPartialName(book, "of Cinder")
    assertEquals(book, entry)
  }

  @Test def `Get two entry by partial name`: Unit = {
    val book = PhoneEntry(1, "Gwyn. Lord of Cinder", "1234342") ::
      PhoneEntry(2, "Soul of Cinder", "1234342") :: Nil
    val entry = getEntriesByPartialName(book, "of Cinder")
    assertEquals(book, entry)
  }

  @Test def `Get no entries by partial name`: Unit = {
    val book = PhoneEntry(1, "Gwyn. Lord of Cinder", "1234342") ::
      PhoneEntry(2, "Soul of Cinder", "1234342") :: Nil
    val entry = getEntriesByPartialName(book, "Aldrich, Saint of the Deep")
    assertEquals(List(), entry)
  }



  @Test def `Get one entry by partial number`: Unit = {
    val book = PhoneEntry(1, "Gwyn. Lord of Cinder", "1234342") :: Nil
    val entry = getEntriesByPartialNumber(book, "123")
    assertEquals(book, entry)
  }

  @Test def `Get two entry by partial number`: Unit = {
    val book = PhoneEntry(1, "Gwyn. Lord of Cinder", "1234342") ::
      PhoneEntry(2, "Soul of Cinder", "1234342") :: Nil
    val entry = getEntriesByPartialNumber(book, "123")
    assertEquals(book, entry)
  }

  @Test def `Get no entries by partial number`: Unit = {
    val book = PhoneEntry(1, "Gwyn. Lord of Cinder", "1234342") ::
      PhoneEntry(2, "Soul of Cinder", "1234342") :: Nil
    val entry = getEntriesByPartialNumber(book, "999123")
    assertEquals(List(), entry)
  }



  @Test def `getNameById gives name back`: Unit = {
    val book = PhoneEntry(1, "John", "1") :: PhoneEntry(2, "Sam", "2") :: Nil
    assertEquals(Right("Sam"), getNameById(book, 2))
  }

  @Test def `getNameById gives error with random id`: Unit = {
    val book = PhoneEntry(1, "John", "1") :: PhoneEntry(2, "Sam", "2") :: Nil
    assertEquals(Left(NoSuchIdInBookError), getNameById(book, 3))
  }

  @Test def `getNameById throws error if there is more than one ID`: Unit = {
    val book = PhoneEntry(1, "1", "1") :: PhoneEntry(1, "2", "2") :: Nil
    val res = getNameById(book, 1)
    assertEquals(Left(MoreThanOneIdError), res)
  }




  @Test def `getPhoneNumberById gives number back`: Unit = {
    val book = PhoneEntry(1, "John", "1") :: PhoneEntry(2, "Sam", "2") :: Nil
    assertEquals(Right("2"), getPhoneNumberById(book, 2))
  }

  @Test def `getPhoneNumberById gives error with random id`: Unit = {
    val book = PhoneEntry(1, "John", "1") :: PhoneEntry(2, "Sam", "2") :: Nil
    assertEquals(Left(NoSuchIdInBookError), getPhoneNumberById(book, 3))
  }

  @Test def `getPhoneNumberById throws error if there is more than one ID`: Unit = {
    val book = PhoneEntry(1, "1", "1") :: PhoneEntry(1, "2", "2") :: Nil
    val res = getPhoneNumberById(book, 1)
    assertEquals(Left(MoreThanOneIdError), res)
  }



  @Test def `changeNameById works`: Unit = {
    val book = PhoneEntry(1, "John", "1") :: PhoneEntry(2, "Sam", "2") :: Nil
    val book2 = PhoneEntry(2, "Stanley", "2") :: PhoneEntry(1, "John", "1") :: Nil
    assertEquals(Right(book2), changeNameById(book, 2, "Stanley"))
  }

  @Test def `changeNameById throws error if name is not valid`: Unit = {
    val book = PhoneEntry(1, "John", "1") :: PhoneEntry(2, "Sam", "2") :: Nil
    val book2 = PhoneEntry(2, "Stanley", "2") :: PhoneEntry(1, "John", "1") :: Nil
    assertEquals(Left(InvalidNameFormat), changeNameById(book, 2, ""))
  }

  @Test def `changeNameById throws error if there is no such id`: Unit = {
    val book = PhoneEntry(1, "John", "1") :: PhoneEntry(2, "Sam", "2") :: Nil
    assertEquals(Left(NoSuchIdInBookError), changeNameById(book, 3, "Stanley"))
  }

  @Test def `changeNameById  throws error if there is more than one ID`: Unit = {
    val book = PhoneEntry(1, "1", "1") :: PhoneEntry(1, "2", "2") :: Nil
    val res = changeNameById(book, 1, "1")
    assertEquals(Left(MoreThanOneIdError), res)
  }



  @Test def `changePhoneNumberById works`: Unit = {
    val book = PhoneEntry(1, "John", "1") :: PhoneEntry(2, "Sam", "2") :: Nil
    val book2 = PhoneEntry(2, "Sam", "123") :: PhoneEntry(1, "John", "1") :: Nil
    assertEquals(Right(book2), changePhoneNumberById(book, 2, "123"))
  }

  @Test def `changePhoneNumberById throws error if name is not valid`: Unit = {
    val book = PhoneEntry(1, "John", "1") :: PhoneEntry(2, "Sam", "2") :: Nil
    val book2 = PhoneEntry(2, "Stanley", "2") :: PhoneEntry(1, "John", "1") :: Nil
    assertEquals(Left(InvalidNumberFormat), changePhoneNumberById(book, 2, ""))
  }

  @Test def `changePhoneNumberById throws error if there is no such id`: Unit = {
    val book = PhoneEntry(1, "John", "1") :: PhoneEntry(2, "Sam", "2") :: Nil
    assertEquals(Left(NoSuchIdInBookError), changePhoneNumberById(book, 3, "Stanley"))
  }

  @Test def `changePhoneNumberById throws error if there is more than one ID`: Unit = {
    val book = PhoneEntry(1, "1", "1") :: PhoneEntry(1, "2", "2") :: Nil
    val res = changePhoneNumberById(book, 1, "1")
    assertEquals(Left(MoreThanOneIdError), res)
  }



  @Test def `Remove one ID from book works`: Unit = {
    val book = PhoneEntry(1, "John", "1") :: PhoneEntry(2, "Sam", "2") :: Nil
    val book2 = PhoneEntry(2, "Sam", "2") :: Nil
    assertEquals(Right(book2), removeEntryFromBookById(book, 1))
  }

  @Test def `Remove multiple ID from book removes all entries`: Unit = {
    val book = PhoneEntry(1, "John", "1") :: PhoneEntry(1, "Sam", "2") :: Nil
    assertEquals(Right(Nil), removeEntryFromBookById(book, 1))
  }

  @Test def `Remove non-existing ID from book throws error`: Unit = {
    val result = removeEntryFromBookById(createBook(), 1)
    assertEquals(Left(NoSuchIdInBookError), result)
  }

}
