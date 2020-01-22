package phonebook

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveEncoder, deriveDecoder}
import java.util.UUID

object PhoneEntryHandler {


  /** Модель для телефонных записей, которая содержит необходимые поля для JSON-а.
   *  Рядом находятся неявные декодер и энкодер, которые будут использоваться для
   *  преобразования записей в JSON и обратно.
   *
   *  @param id уникальный UUID данного телефонного номера
   *  @param name имя владельца телефона
   *  @param phoneNumber телефонный номер
   */
  case class PhoneEntry(id: Long, name: String, phoneNumber: String)


  implicit val entryEncoder: Encoder[PhoneEntry] = deriveEncoder[PhoneEntry]
  implicit val entryDecoder: Decoder[PhoneEntry] = deriveDecoder[PhoneEntry]

}
