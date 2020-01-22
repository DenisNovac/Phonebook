package phonebook

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveEncoder, deriveDecoder}

object ContactHandler {


  /** Модель для телефонных записей, которая содержит необходимые поля для JSON-а.
   *  Рядом находятся неявные декодер и энкодер, которые будут использоваться для
   *  преобразования записей в JSON и обратно.
   *
   *  @param id уникальный UUID данного телефонного номера
   *  @param name имя владельца телефона
   *  @param phoneNumber телефонный номер
   */
  case class Contact(id: Long, name: String, phoneNumber: String)
  implicit val contactEncoder: Encoder[Contact] = deriveEncoder[Contact]
  implicit val contactDecoder: Decoder[Contact] = deriveDecoder[Contact]

  /** Модель для запросов на добавление телефонных записей. Её основное назначение -
   *  получать от пользователя (пользователь не может генерировать ID сам)/
   * @param name имя владельца телефона
   * @param phoneNumber телефонный номер
   */
  case class ContactRequest(name: String, phoneNumber: String)
  implicit val requestEncoder: Encoder[ContactRequest] = deriveEncoder[ContactRequest]
  implicit val requestDecoder: Decoder[ContactRequest] = deriveDecoder[ContactRequest]

}
