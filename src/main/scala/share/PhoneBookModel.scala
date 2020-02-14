package share

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import share.ContactModel.Contact

object PhoneBookModel {
  /** Тип для удобства обращения */
  type PhoneBook = List[Contact]

  /** Модель JSON для телефонной книги. Рядом находятся энкодер и декодер для
    * преобразования в JSON или обратно
    * @param items телефонная книга, которую требуется вывести в JSON
    */
  case class PhoneBookModel(items: PhoneBook)
  implicit val bookEncoder: Encoder[PhoneBookModel] = deriveEncoder[PhoneBookModel]
  implicit val bookDecoder: Decoder[PhoneBookModel] = deriveDecoder[PhoneBookModel]



}
