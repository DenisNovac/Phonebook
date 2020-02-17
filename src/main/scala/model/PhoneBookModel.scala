package model

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import model.ContactModel.Contact

object PhoneBookModel {

  /** Модель JSON для телефонной книги. Рядом находятся энкодер и декодер для
    * преобразования в JSON или обратно
    * @param items телефонная книга, которую требуется вывести в JSON
    */
  case class PhoneBookModel(items: List[Contact])
  implicit val bookEncoder: Encoder[PhoneBookModel] = deriveEncoder[PhoneBookModel]
  implicit val bookDecoder: Decoder[PhoneBookModel] = deriveDecoder[PhoneBookModel]
}
