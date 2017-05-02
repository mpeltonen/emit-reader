package emitreader.domain

import java.time.format.DateTimeFormatter
import java.time.{Instant, ZoneId}

case class EmitData(cardId: Long, readingTime: Long, punches: Seq[Punch]) {
  lazy val formattedReadingTime: String = {
    val localDateTime = Instant.ofEpochMilli(readingTime).atZone(ZoneId.systemDefault()).toLocalDateTime()
    DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(localDateTime)
  }

  lazy val readoutSplitTimeSec: Long = punches.last.splitTime
  lazy val lastControlSplitTimeSec: Long = punches.reverse.dropWhile(_.controlCode == 250).head.splitTime
  lazy val lastControlPunchTime: Long = readingTime - ((readoutSplitTimeSec - lastControlSplitTimeSec) * 1000)

  def punchingTime(punch: Punch): Long = {
    readingTime - ((readoutSplitTimeSec - punches.find(_ == punch).get.splitTime) * 1000)
  }
}
