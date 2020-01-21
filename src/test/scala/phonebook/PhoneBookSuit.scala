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
    val book = PhoneEntry(randomUUID(), "Harry Mason", "821-223") ::
      PhoneEntry(randomUUID(), "James Sunderland", "324-223") :: Nil

    val json = bookToJson(book)
    val decoded = jsonToBook(json)

    assertEquals(Right(book), decoded)
  }



  @Test def `Get one entry by partial name`: Unit = {
    val book = PhoneEntry(randomUUID(), "Gwyn. Lord of Cinder", "1234342") :: Nil

    val entry = getEntriesByPartialName(book, "of Cinder")
    assertEquals(book, entry)
  }

  @Test def `Get two entry by partial name`: Unit = {
    val book = PhoneEntry(randomUUID(), "Gwyn. Lord of Cinder", "1234342") ::
      PhoneEntry(randomUUID(), "Soul of Cinder", "1234342") :: Nil

    val entry = getEntriesByPartialName(book, "of Cinder")
    assertEquals(book, entry)
  }

  @Test def `Get no entries by partial name`: Unit = {
    val book = PhoneEntry(randomUUID(), "Gwyn. Lord of Cinder", "1234342") ::
      PhoneEntry(randomUUID(), "Soul of Cinder", "1234342") :: Nil

    val entry = getEntriesByPartialName(book, "Aldrich, Saint of the Deep")
    assertEquals(List(), entry)
  }



  @Test def `Get one entry by partial number`: Unit = {
    val book = PhoneEntry(randomUUID(), "Gwyn. Lord of Cinder", "1234342") :: Nil

    val entry = getEntriesByPartialNumber(book, "123")
    assertEquals(book, entry)
  }

  @Test def `Get two entry by partial number`: Unit = {
    val book = PhoneEntry(randomUUID(), "Gwyn. Lord of Cinder", "1234342") ::
      PhoneEntry(randomUUID(), "Soul of Cinder", "1234342") :: Nil

    val entry = getEntriesByPartialNumber(book, "123")
    assertEquals(book, entry)
  }

  @Test def `Get no entries by partial number`: Unit = {
    val book = PhoneEntry(randomUUID(), "Gwyn. Lord of Cinder", "1234342") ::
      PhoneEntry(randomUUID(), "Soul of Cinder", "1234342") :: Nil

    val entry = getEntriesByPartialNumber(book, "999123")
    assertEquals(List(), entry)
  }



  @Test def `getNameById gives name back`: Unit = {
    val uuid1 = fromString("4cc78407-cea5-451f-8631-b615a254c7e3")
    val uuid2 = fromString("3cc78407-cea5-451f-8631-b615a254c7e3")
    val book = PhoneEntry(uuid1, "John", "1") :: PhoneEntry(uuid2, "Sam", "2") :: Nil

    assertEquals(Right("Sam"), getNameById(book, uuid2.toString))
  }

  @Test def `getNameById gives error with random id`: Unit = {
    val uuid1 = fromString("4cc78407-cea5-451f-8631-b615a254c7e3")
    val uuid2 = fromString("3cc78407-cea5-451f-8631-b615a254c7e3")
    val book = PhoneEntry(uuid1, "John", "1") :: PhoneEntry(uuid2, "Sam", "2") :: Nil

    assertEquals(Left(NoSuchIdInBookError), getNameById(book, "fdgkfdog"))
  }

  @Test def `getNameById throws error if there is more than one ID`: Unit = {
    val uuid = fromString("4cc78407-cea5-451f-8631-b615a254c7e3")
    val book = PhoneEntry(uuid, "1", "1") :: PhoneEntry(uuid, "2", "2") :: Nil
    val res = getNameById(book, uuid.toString)
    assertEquals(Left(MoreThanOneIdError), res)
  }




  @Test def `getPhoneNumberById gives number back`: Unit = {
    val uuid1 = fromString("4cc78407-cea5-451f-8631-b615a254c7e3")
    val uuid2 = fromString("3cc78407-cea5-451f-8631-b615a254c7e3")
    val book = PhoneEntry(uuid1, "John", "1") :: PhoneEntry(uuid2, "Sam", "2") :: Nil

    assertEquals(Right("2"), getPhoneNumberById(book, uuid2.toString))
  }

  @Test def `getPhoneNumberById gives error with random id`: Unit = {
    val uuid1 = fromString("4cc78407-cea5-451f-8631-b615a254c7e3")
    val uuid2 = fromString("3cc78407-cea5-451f-8631-b615a254c7e3")
    val book = PhoneEntry(uuid1, "John", "1") :: PhoneEntry(uuid2, "Sam", "2") :: Nil

    assertEquals(Left(NoSuchIdInBookError), getPhoneNumberById(book, "fdgkfdog"))
  }

  @Test def `getPhoneNumberById throws error if there is more than one ID`: Unit = {
    val uuid = fromString("4cc78407-cea5-451f-8631-b615a254c7e3")
    val book = PhoneEntry(uuid, "1", "1") :: PhoneEntry(uuid, "2", "2") :: Nil
    val res = getPhoneNumberById(book, uuid.toString)
    assertEquals(Left(MoreThanOneIdError), res)
  }



  @Test def `changeNameById works`: Unit = {
    val uuid1 = fromString("4cc78407-cea5-451f-8631-b615a254c7e3")
    val uuid2 = fromString("3cc78407-cea5-451f-8631-b615a254c7e3")
    val book = PhoneEntry(uuid1, "John", "1") :: PhoneEntry(uuid2, "Sam", "2") :: Nil
    val book2 = PhoneEntry(uuid2, "Stanley", "2") :: PhoneEntry(uuid1, "John", "1") :: Nil

    assertEquals(Right(book2), changeNameById(book, uuid2.toString, "Stanley"))
  }

  @Test def `changeNameById throws error if name is not valid`: Unit = {
    val uuid1 = fromString("4cc78407-cea5-451f-8631-b615a254c7e3")
    val uuid2 = fromString("3cc78407-cea5-451f-8631-b615a254c7e3")
    val book = PhoneEntry(uuid1, "John", "1") :: PhoneEntry(uuid2, "Sam", "2") :: Nil
    val book2 = PhoneEntry(uuid2, "Stanley", "2") :: PhoneEntry(uuid1, "John", "1") :: Nil

    assertEquals(Left(InvalidNameFormat), changeNameById(book, uuid2.toString, ""))
  }

  @Test def `changeNameById throws error if there is no such id`: Unit = {
    val uuid1 = fromString("4cc78407-cea5-451f-8631-b615a254c7e3")
    val uuid2 = fromString("3cc78407-cea5-451f-8631-b615a254c7e3")
    val book = PhoneEntry(uuid1, "John", "1") :: PhoneEntry(uuid2, "Sam", "2") :: Nil

    assertEquals(Left(NoSuchIdInBookError), changeNameById(book, "sdofksdof", "Stanley"))
  }

  @Test def `changeNameById  throws error if there is more than one ID`: Unit = {
    val uuid = fromString("4cc78407-cea5-451f-8631-b615a254c7e3")
    val book = PhoneEntry(uuid, "1", "1") :: PhoneEntry(uuid, "2", "2") :: Nil
    val res = changeNameById(book, uuid.toString, "1")
    assertEquals(Left(MoreThanOneIdError), res)
  }



  @Test def `changePhoneNumberById works`: Unit = {
    val uuid1 = fromString("4cc78407-cea5-451f-8631-b615a254c7e3")
    val uuid2 = fromString("3cc78407-cea5-451f-8631-b615a254c7e3")
    val book = PhoneEntry(uuid1, "John", "1") :: PhoneEntry(uuid2, "Sam", "2") :: Nil
    val book2 = PhoneEntry(uuid2, "Sam", "123") :: PhoneEntry(uuid1, "John", "1") :: Nil

    assertEquals(Right(book2), changePhoneNumberById(book, uuid2.toString, "123"))
  }

  @Test def `changePhoneNumberById throws error if name is not valid`: Unit = {
    val uuid1 = fromString("4cc78407-cea5-451f-8631-b615a254c7e3")
    val uuid2 = fromString("3cc78407-cea5-451f-8631-b615a254c7e3")
    val book = PhoneEntry(uuid1, "John", "1") :: PhoneEntry(uuid2, "Sam", "2") :: Nil
    val book2 = PhoneEntry(uuid2, "Stanley", "2") :: PhoneEntry(uuid1, "John", "1") :: Nil

    assertEquals(Left(InvalidNumberFormat), changePhoneNumberById(book, uuid2.toString, ""))
  }

  @Test def `changePhoneNumberById throws error if there is no such id`: Unit = {
    val uuid1 = fromString("4cc78407-cea5-451f-8631-b615a254c7e3")
    val uuid2 = fromString("3cc78407-cea5-451f-8631-b615a254c7e3")
    val book = PhoneEntry(uuid1, "John", "1") :: PhoneEntry(uuid2, "Sam", "2") :: Nil

    assertEquals(Left(NoSuchIdInBookError), changePhoneNumberById(book, "sdofksdof", "Stanley"))
  }

  @Test def `changePhoneNumberById throws error if there is more than one ID`: Unit = {
    val uuid = fromString("4cc78407-cea5-451f-8631-b615a254c7e3")
    val book = PhoneEntry(uuid, "1", "1") :: PhoneEntry(uuid, "2", "2") :: Nil
    val res = changePhoneNumberById(book, uuid.toString, "1")
    assertEquals(Left(MoreThanOneIdError), res)
  }



  @Test def `Remove one ID from book works`: Unit = {
    val uuid1 = fromString("4cc78407-cea5-451f-8631-b615a254c7e3")
    val uuid2 = fromString("3cc78407-cea5-451f-8631-b615a254c7e3")
    val book = PhoneEntry(uuid1, "John", "1") :: PhoneEntry(uuid2, "Sam", "2") :: Nil
    val book2 = PhoneEntry(uuid2, "Sam", "2") :: Nil

    assertEquals(Right(book2), removeEntryFromBookById(book, uuid1.toString))
  }

  @Test def `Remove multiple ID from book removes all entries`: Unit = {
    val uuid = fromString("4cc78407-cea5-451f-8631-b615a254c7e3")
    val book = PhoneEntry(uuid, "John", "1") :: PhoneEntry(uuid, "Sam", "2") :: Nil

    assertEquals(Right(Nil), removeEntryFromBookById(book, uuid.toString))
  }

  @Test def `Remove non-existing ID from book throws error`: Unit = {
    val result = removeEntryFromBookById(createBook(), "random uuid")
    assertEquals(Left(NoSuchIdInBookError), result)
  }

}
