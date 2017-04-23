package emitreader.targets.rogainmanager

import scalafx.beans.property.{Property, StringProperty}
import scalafx.collections.ObservableBuffer

class RogainManagerTargetViewModel {
  val ipAddress: Property[String, String] = new StringProperty("192.168.141.131")
  val logBuffer: ObservableBuffer[CharSequence] = new ObservableBuffer()
}
