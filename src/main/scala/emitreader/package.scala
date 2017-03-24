import java.util.Date

package object emitreader {
  case class EmitCard(cardId: Long, readingTime: Long, punches: Seq[Punch]) {
    override def toString(): String = {
      val readingTimeStr = new Date(readingTime).toString()
      val punchesStr = punches.map(p => s"* ${p.toString}").mkString("\n")
      s"Card Id: ${cardId}, Reading Time: ${readingTimeStr}\n${punchesStr}"
    }
  }
  case class Punch(controlCode: Int, splitTime: Long, lowBattery: Boolean) {
    override def toString(): String = {
      val (splitTimeMin, splitTimeSec) = (splitTime / 60, splitTime % 60)
      s"Code: ${controlCode}, Time: ${splitTimeMin} min ${splitTimeSec} sec${if (lowBattery) " (LOW BATTERY!)" else ""}"
    }
  }
}
