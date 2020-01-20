package phonebook

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveEncoder, deriveDecoder}
import java.util.UUID

object PhoneEntryHandler {
  /** Менеджер для телефонных записей, который содержит необходимые поля для JSON-а
    *  и неявные параметры, передаваемые при каждом вызове метода asJson
    *
    * @param id уникальный UUID данного телефонного номера
    * @param name имя владельца телефона
    * @param phoneNumber телефонный номер
    */

  case class PhoneEntry(id: UUID, name: String, phoneNumber: String)

  implicit val encoder: Encoder[PhoneEntry] = deriveEncoder[PhoneEntry]
  implicit val decoder: Decoder[PhoneEntry] = deriveDecoder[PhoneEntry]

}
