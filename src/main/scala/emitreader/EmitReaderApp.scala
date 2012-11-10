package emitreader

import java.util.Date
import Decoder._

object EmitReaderApp {
  val comPortName = "tty.PL2303"

  def main(args: Array[String]) {
    val decoder = args match {
      case Array("time") => Decoder.timeOnly
      case _             => Decoder.full
    }
    def onEmitData = { (readTime: ReadingTime, emitCardId: EmitCardId, punches: Punches) =>
      println("Card id: %d, Read time: %s" format (emitCardId, new Date(readTime).toString))
      if (punches.nonEmpty) {
        println("(control code, split time)\n--------------------------")
        println(punches.map(_.toString()).mkString("\n"))
      }
    }

    new EmitReaderUnit(comPortName, onEmitData, decoder = decoder)

    while (true) Thread.sleep(100)
  }
}
