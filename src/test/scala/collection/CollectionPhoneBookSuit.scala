package collection

import collection.CollectionPhoneBookHandler._
import org.junit.Assert._
import org.junit._
import share.ContactModel._
import share._

class CollectionPhoneBookSuit {

  @Test def `Insert empty name is not possible`: Unit = {
    val result = addContact(createBook(), ContactRequest("", "12345"))
    assertEquals(Left(InvalidInput), result)
  }

  @Test def `Insert empty number is not possible`: Unit = {
    val result = addContact(createBook(), ContactRequest("Josh", ""))
    assertEquals(Left(InvalidInput), result)
  }

  @Test def `Inserting in book works`: Unit = {
    val name = "James Sunderland"
    val phone = "883-223"
    val result = addContact(createBook(), ContactRequest(name, phone))
    assertEquals(Right(List(Contact(1, name, phone))), result)
  }



  @Test def `Book to json and backwards works`: Unit = {
    val book = Contact(1, "Harry Mason", "821-223") ::
      Contact(2, "James Sunderland", "324-223") :: Nil
    val json = listContacts(book)
    val decoded = jsonToBook(json)
    assertEquals(Right(book), decoded)
  }



  @Test def `Get entry by partial name validates name`: Unit = {
    val book = Contact(1, "Gwyn. Lord of Cinder", "1234342") :: Nil
    val entry = findContactsByName(book, List(""))
    assertEquals(Left(InvalidNameValue), entry)
  }

  @Test def `Get one entry by partial name`: Unit = {
    val book = Contact(1, "Gwyn, Lord of Cinder", "1234342") :: Nil
    val entry = findContactsByName(book, List("Gwyn"))
    assertEquals(Right(book), entry)
  }

  @Test def `Get two entry by partial name`: Unit = {
    val book = Contact(1, "Gwyn, Lord of Cinder", "1234342") ::
      Contact(2, "Gwyn, Lord of Sunlight", "1234342") :: Nil
    val entry = findContactsByName(book, List("Gwyn"))
    assertEquals(Right(book), entry)
  }

  @Test def `Get no entries by partial name`: Unit = {
    val book = Contact(1, "Gwyn. Lord of Cinder", "1234342") ::
      Contact(2, "Soul of Cinder", "1234342") :: Nil
    val entry = findContactsByName(book, List("Aldrich, Saint of the Deep"))
    assertEquals(Right(List()), entry)
  }



  @Test def `Get entry by partial phone validates phone`: Unit = {
    val book = Contact(1, "Gwyn. Lord of Cinder", "1234342") ::
      Contact(2, "Soul of Cinder", "1234342") :: Nil
    val entry = findContactsByPhone(book, List(""))
    assertEquals(Left(InvalidPhoneValue), entry)
  }

  @Test def `Get one entry by partial number`: Unit = {
    val book = Contact(1, "Gwyn. Lord of Cinder", "1234342") :: Nil
    val entry = findContactsByPhone(book, List("123"))
    assertEquals(Right(book), entry)
  }

  @Test def `Get two entry by partial number`: Unit = {
    val book = Contact(1, "Gwyn. Lord of Cinder", "1234342") ::
      Contact(2, "Soul of Cinder", "1234342") :: Nil
    val entry = findContactsByPhone(book, List("123"))
    assertEquals(Right(book), entry)
  }

  @Test def `Get no entries by partial number`: Unit = {
    val book = Contact(1, "Gwyn. Lord of Cinder", "1234342") ::
      Contact(2, "Soul of Cinder", "1234342") :: Nil
    val entry = findContactsByPhone(book, List("999123"))
    assertEquals(Right(List()), entry)
  }



  @Test def `Getting contacts by ID works`: Unit = {
    val book = Contact(1, "John", "1") :: Contact(2, "Sam", "2") :: Nil
    assertEquals(Right(Contact(2, "Sam", "2")), getContactById(book, 2))
  }

  @Test def `Getting contacts by ID gives error with random id`: Unit = {
    val book = Contact(1, "John", "1") :: Contact(2, "Sam", "2") :: Nil
    assertEquals(Left(ContactNotFound), getContactById(book, 3))
  }



  @Test def `Updating contact name works`: Unit = {
    val book = Contact(1, "John", "1") :: Contact(2, "Sam", "2") :: Nil
    val book2 = Contact(2, "Stanley", "2") :: Contact(1, "John", "1") :: Nil
    assertEquals(Right(book2), updateContact(book, 2, ContactRequest("Stanley", "2")))
  }

  @Test def `Updating contact phone works`: Unit = {
    val book = Contact(1, "John", "1") :: Contact(2, "Sam", "2") :: Nil
    val book2 = Contact(2, "Sam", "123") :: Contact(1, "John", "1") :: Nil
    assertEquals(Right(book2), updateContact(book, 2, ContactRequest("Sam", "123")))
  }

  @Test def `Updating contact throws error if name is not valid`: Unit = {
    val book = Contact(1, "John", "1") :: Contact(2, "Sam", "2") :: Nil
    assertEquals(Left(InvalidInput), updateContact(book, 2, ContactRequest("", "23423")))
  }

  @Test def `Updating contact throws error if phone is not valid`: Unit = {
    val book = Contact(1, "John", "1") :: Contact(2, "Sam", "2") :: Nil
    assertEquals(Left(InvalidInput), updateContact(book, 2, ContactRequest("Stanley", "")))
  }

  @Test def `Updating contact throws error if there is no such id`: Unit = {
    val book = Contact(1, "John", "1") :: Contact(2, "Sam", "2") :: Nil
    assertEquals(Left(InvalidInput), updateContact(book, 3, ContactRequest( "Stanley", "2")))
  }



  @Test def `Remove one ID from book works`: Unit = {
    val book = Contact(1, "John", "1") :: Contact(2, "Sam", "2") :: Nil
    val book2 = Contact(2, "Sam", "2") :: Nil
    assertEquals(Right(book2), deleteContact(book, 1))
  }

  @Test def `Remove multiple ID from book throws error`: Unit = {
    val book = Contact(1, "John", "1") :: Contact(1, "Sam", "2") :: Nil
    assertEquals(Left(InvalidIdSupplied), deleteContact(book, 1))
  }

  @Test def `Remove non-existing ID from book throws error`: Unit = {
    val result = deleteContact(createBook(), 1)
    assertEquals(Left(ContactNotFound), result)
  }

}
