package phonebook

import java.util.concurrent.atomic.AtomicLong

object IdGenerator {
  /** Генератор уникальных ID. Основан на AtomicLong.
   *  TODO: Переписать функционально
   */
  val id: AtomicLong = new AtomicLong()
  def next(): Long = id.getAndIncrement()
}
