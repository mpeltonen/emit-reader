package emitreader

import akka.actor.IO
import akka.util.ByteString
import java.nio.ByteOrder

object Decoder {
  def full(callback: (Long, Int, Seq[(Int, Int)]) => Unit): IO.Iteratee[Unit] = {
    for {
      prefix <- IO.takeUntil(ByteString(0xFF.toByte, 0xFF.toByte), true)
      id <- IO.take(3)
      unused1 <- IO.take(1)
      cardProductionWeek <- IO.take(1)
      cardProductionYear <- IO.take(1)
      unused2 <- IO.take(1)
      checksum1 <- IO.take(1)
      punchingData <- IO.take(150)
      unused3 <- IO.take(56)
      checksum2 <- IO.take(1)
    } yield {
      val allBytes = prefix ++ id ++ unused1 ++ cardProductionWeek ++ cardProductionYear ++ unused2 ++ checksum1 ++ punchingData ++ unused3 ++ checksum2
      if (isValidPacket(allBytes)) {
        val iterator = punchingData.iterator
        val punches = for (i <- 0 until 50) yield (unsigned(iterator.getByte), iterator.getShort(ByteOrder.LITTLE_ENDIAN).toInt)
        callback(System.currentTimeMillis, decodeId(id), punches)
      }
    }
  }

  def timeOnly(callback: (Long, Int, Seq[(Int, Int)]) => Unit): IO.Iteratee[Unit] = {
    for {
      prefix <- IO.takeUntil(ByteString(0xFF.toByte, 0xFF.toByte), true)
      id <- IO.take(3)
      unused1 <- IO.take(1)
      cardProductionWeek <- IO.take(1)
      cardProductionYear <- IO.take(1)
      unused2 <- IO.take(1)
      checksum1 <- IO.take(1)
    } yield {
      val allBytes = id ++ unused1 ++ cardProductionWeek ++ cardProductionYear ++ unused2 ++ checksum1
      if (isValidPacket(allBytes)) {
        callback(System.currentTimeMillis, decodeId(id), Seq())
      }
    }
  }

  def isValidPacket(bs: ByteString) = bs.nonEmpty && bs.sum % 256 == 0
  def decodeId(bs: ByteString) = (unsigned(bs(0)) | unsigned(bs(1)) << 8 | unsigned(bs(2)) << 16)
  def unsigned(b: Byte) = 0xff & b
}
