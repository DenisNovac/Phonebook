package collection

import java.util.concurrent.atomic.AtomicLong

object IdGenerator {
  /** Генератор уникальных ID. Основан на AtomicLong.
   *  TODO: Переписать функционально
   */
  val id: AtomicLong = new AtomicLong(1)  // PostgreSQL начинает с 1, для единообразия будем тоже
  def next(): Long = id.getAndIncrement()
  def reset(): Unit = id.set(1)  // для тестов API
}
