
package object emitreader {
  case class PunchCardData(readingTime: Long, cardId: Long, punches: Seq[Punch])
  case class Punch(controlCode: Int, splitTime: Long, lowBattery: Boolean)
}
