package emitreader

import purejavacomm.CommPortIdentifier
import purejavacomm.CommPortIdentifier.getPortIdentifiers

import scala.collection.JavaConverters._

object EmitReaderApp {
  def main(args: Array[String]) {
    val frameLength = args match {
      case Array("time") => 10
      case _ => 217
    }

    val comPorts: List[CommPortIdentifier] = getPortIdentifiers.asScala.filter(_.getName.startsWith("cu")).toList
    val comPort = comPorts.find(_.getName.contains("PL2303")).getOrElse(sys.error("PL2303 serial port not found!"))

    new EmitReaderUnit(comPort.getName, frameLength, { (data: EmitCard) => println(data) })

    while (true) Thread.sleep(100)
  }
}
