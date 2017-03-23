package emitreader

import java.util.Date

import purejavacomm.CommPortIdentifier
import purejavacomm.CommPortIdentifier.getPortIdentifiers

import scala.collection.JavaConverters._

object EmitReaderApp {
  def main(args: Array[String]) {
    val frameLength = args match {
      case Array("time") => 10
      case _ => 217
    }

    val comPorts: List[CommPortIdentifier] = getPortIdentifiers.asScala.filter(p => p.getName.startsWith("tty")).toList
    val comPort = comPorts.find(_.getName.contains("PL2303")).getOrElse(sys.error("PL2303 serial port not found!"))

    def onEmitData  = { cardData: PunchCardData =>
      println("Card id: %d, Read time: %s" format (cardData.cardId, new Date(cardData.readingTime).toString))
      val punches = cardData.punches
      if (punches.nonEmpty) {
        println("(control code, split time, low battery)\n--------------------------")
        println(punches.map(_.toString()).mkString("\n"))
      }
    }

    new EmitReaderUnit(comPort.getName, frameLength, onEmitData)

    while (true) Thread.sleep(100)
  }
}
