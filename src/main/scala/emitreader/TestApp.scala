package emitreader

import akka.actor.{Props, ActorSystem}
import java.util.Date

object TestApp {
  def main(args: Array[String]) {
    def onEmitData = { (readTime: Long, emitCardId: Int, controls: Seq[(Int, Int)]) =>
      println("Card id: %d, Read time: %s" format (emitCardId, new Date(readTime).toString))
      println("(control code, split time)\n--------------------------")
      println(controls.map(_.toString()).mkString("\n"))
    }

    new EmitReaderUnit("tty.PL2303", onEmitData)

    while (true) {
      Thread.sleep(100)
    }
  }
}
