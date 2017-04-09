package emitreader.sources.binaryfile

import scalafx.beans.property.{IntegerProperty, Property, StringProperty}

class BinaryFileSourceViewModel {
  val fileName: Property[String, String] = new StringProperty()
  val sendDataClickCount: Property[Int, Number] = new IntegerProperty()
}
