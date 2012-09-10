package emitreader

import java.util.Date

object EmitReaderApp {
  val comPortName = "tty.PL2303"

  def main(args: Array[String]) {
    val decoder = args match {
      case Array("time") => Decoder.timeOnly
      case _             => Decoder.full
    }
    def onEmitData = { (readTime: Long, emitCardId: Int, controls: Seq[(Int, Int)]) =>
      println("Card id: %d, Read time: %s" format (emitCardId, new Date(readTime).toString))
      if (controls.nonEmpty) {
        println("(control code, split time)\n--------------------------")
        println(controls.map(_.toString()).mkString("\n"))
      }
    }

    new EmitReaderUnit(comPortName, onEmitData, decoder = decoder)

    while (true) Thread.sleep(100)
  }
}
