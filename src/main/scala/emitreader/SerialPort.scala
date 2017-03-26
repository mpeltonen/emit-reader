package emitreader

import scala.collection.JavaConverters._
import purejavacomm.SerialPortEvent
import purejavacomm.SerialPort.{DATABITS_8, STOPBITS_1, PARITY_NONE, FLOWCONTROL_XONXOFF_IN}
import purejavacomm.CommPortIdentifier.getPortIdentifiers
import java.io.InputStream

class SerialPort(portName: String) {
  val portId = getPortIdentifiers.asScala.find(_.getName.equals(portName)).getOrElse(sys.error("Serial port %s not found" format portName))
  val port = portId.open(this.getClass.getName, 1000).asInstanceOf[purejavacomm.SerialPort]
  port.setSerialPortParams(9600, DATABITS_8, STOPBITS_1, PARITY_NONE)
  port.setFlowControlMode(FLOWCONTROL_XONXOFF_IN)

  def close(): Unit = port.close()

  def onDataAvailable(callback: (InputStream) => Unit): Unit = {
    port.notifyOnDataAvailable(true)
    port.addEventListener((event: SerialPortEvent) =>
      if (event.getEventType == SerialPortEvent.DATA_AVAILABLE) callback(port.getInputStream))
  }
}
