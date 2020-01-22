package phonebook

import java.util.concurrent.atomic.AtomicLong

object IdGenerator {
  val id: AtomicLong = new AtomicLong()
  def next(): Long = id.getAndIncrement()
}
