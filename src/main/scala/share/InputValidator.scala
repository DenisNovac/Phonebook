package share

object InputValidator {

  def isNameValid(name: String): Boolean =
    !name.isBlank

  def isPhoneValid(number: String): Boolean =
    !number.isBlank
}
