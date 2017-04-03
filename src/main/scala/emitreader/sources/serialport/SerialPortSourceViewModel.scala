package emitreader.sources.serialport

import scalafx.beans.property.{Property, StringProperty}
import scalafx.collections.ObservableBuffer

class SerialPortSourceViewModel {
  val serialPortNames: ObservableBuffer[String] = ObservableBuffer()
  val selectedSerialPortName: Property[String, String] = StringProperty("")
}
