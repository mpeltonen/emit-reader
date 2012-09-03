package emitreader

import akka.actor.{Props, ActorSystem}
import java.util.Date

object TestApp {
  def main(args: Array[String]) {
    val decoder = args match {
      case Array("time", _*) =>  Decoder.timeOnly _
      case _                 => Decoder.full _
    }
    def onEmitData = { (readTime: Long, emitCardId: Int, controls: Seq[(Int, Int)]) =>
      println("Card id: %d, Read time: %s" format (emitCardId, new Date(readTime).toString))
      if (controls.nonEmpty) {
        println("(control code, split time)\n--------------------------")
        println(controls.map(_.toString()).mkString("\n"))
      }
    }

    new EmitReaderUnit("tty.PL2303", onEmitData, decoder = decoder)

    while (true) {
      Thread.sleep(100)
    }
  }
}
