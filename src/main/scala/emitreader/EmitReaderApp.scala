package emitreader

import java.util.Date
import emitreader.Pipeline.{AllControls, SplitTimeOnly}

object EmitReaderApp {
  val comPortName = "tty.PL2303"

  def main(args: Array[String]) {
    val ctx = args match {
      case Array("time") => SplitTimeOnly
      case _ => AllControls
    }

    def onEmitData  = { data: ((ReadingTime, EmitCardId, Punches)) =>
      val (readingTime, cardId, punches) = data
      println("Card id: %d, Read time: %s" format (cardId, new Date(readingTime).toString))
      if (punches.nonEmpty) {
        println("(control code, split time, low battery)\n--------------------------")
        println(punches.map(_.toString()).mkString("\n"))
      }
    }

    new EmitReaderUnit(comPortName, ctx, onEmitData)

    while (true) Thread.sleep(100)
  }
}
