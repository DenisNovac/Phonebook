package phonebook

sealed trait PhoneBookError

object InvalidIdSupplied extends PhoneBookError  // 400
object InvalidNameValue extends PhoneBookError  // 400
object InvalidPhoneValue extends PhoneBookError  // 400
object ContactNotFound extends PhoneBookError  // 404
object InvalidInput extends PhoneBookError  // 405


