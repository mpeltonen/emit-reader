package emitreader

import scala.collection.JavaConverters._
import purejavacomm.{SerialPortEvent, SerialPortEventListener, CommPortIdentifier}
import purejavacomm.SerialPort.{DATABITS_8, STOPBITS_1, PARITY_NONE, FLOWCONTROL_XONXOFF_IN}
import purejavacomm.CommPortIdentifier.getPortIdentifiers
import java.io.InputStream

class SerialPort(portName: String) {
  val portId = getPortIdentifiers.asScala.map(_.asInstanceOf[CommPortIdentifier]).find(_.getName.startsWith(portName)).getOrElse(sys.error("Serial port %s not found" format portName))
  val port = portId.open(this.getClass.getName, 1000).asInstanceOf[purejavacomm.SerialPort]
  port.setSerialPortParams(9600, DATABITS_8, STOPBITS_1, PARITY_NONE)
  port.setFlowControlMode(FLOWCONTROL_XONXOFF_IN)

  def onDataAvailable(callback: (InputStream) => Unit): Unit = {
    port.notifyOnDataAvailable(true)
    port.addEventListener(new SerialPortEventListener() {
      def serialEvent(event: SerialPortEvent): Unit = if (event.getEventType == SerialPortEvent.DATA_AVAILABLE) callback(port.getInputStream)
    })
  }
}
