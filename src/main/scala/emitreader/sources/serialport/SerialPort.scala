package emitreader.sources.serialport

import java.io.InputStream

import purejavacomm.CommPortIdentifier.getPortIdentifiers
import purejavacomm.SerialPort.{DATABITS_8, FLOWCONTROL_XONXOFF_IN, PARITY_NONE, STOPBITS_1}
import purejavacomm.SerialPortEvent

import scala.collection.JavaConverters._

class SerialPort(portName: String, onDataAvailable: (InputStream) => Unit) {
  private val portIdentifier = getPortIdentifiers.asScala
    .find(_.getName.equals(portName))
    .getOrElse(sys.error("Serial port %s not found".format(portName)))
  private val port = portIdentifier.open(this.getClass.getName, 1000).asInstanceOf[purejavacomm.SerialPort]

  port.setSerialPortParams(9600, DATABITS_8, STOPBITS_1, PARITY_NONE)
  port.setFlowControlMode(FLOWCONTROL_XONXOFF_IN)
  port.notifyOnDataAvailable(true)
  port.addEventListener((event: SerialPortEvent) => {
    if (event.getEventType == SerialPortEvent.DATA_AVAILABLE) onDataAvailable(port.getInputStream)
  })

  def close(): Unit = {
    port.close()
  }
}
