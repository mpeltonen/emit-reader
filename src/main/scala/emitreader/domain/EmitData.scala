package emitreader.domain

import java.time.format.DateTimeFormatter
import java.time.{Instant, ZoneId}

case class EmitData(cardId: Long, readingTime: Long, punches: Seq[Punch]) {
  val formattedReadingTime: String = {
    val localDateTime = Instant.ofEpochMilli(readingTime).atZone(ZoneId.systemDefault()).toLocalDateTime()
    DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(localDateTime)
  }
}
