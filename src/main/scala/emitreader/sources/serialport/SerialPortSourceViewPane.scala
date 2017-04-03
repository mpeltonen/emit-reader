package emitreader.sources.serialport

import emitreader.ui.ViewModel
import purejavacomm.CommPortIdentifier
import purejavacomm.CommPortIdentifier.getPortIdentifiers

import scala.collection.JavaConverters._
import scalafx.scene.control.{ComboBox, Label}
import scalafx.scene.layout.VBox

class SerialPortSourceViewPane(globalViewModel: ViewModel, localViewModel: SerialPortSourceViewModel) extends VBox {
  val serialPortLabel = new Label("Serial port:")
  val serialPortSelection = new ComboBox[String](localViewModel.serialPortNames) {
    localViewModel.selectedSerialPortName <== selectionModel().selectedItemProperty()
  }

  localViewModel.serialPortNames.appendAll(getSerialPortNames())

  serialPortSelection.selectionModel().selectFirst()
  serialPortSelection.maxWidth = Double.MaxValue

  children = Seq(serialPortLabel, serialPortSelection)

  def getSerialPortNames(): Seq[String] = getPortIdentifiers().asScala
    .filter(_.getPortType() == CommPortIdentifier.PORT_SERIAL)
    .filterNot(_.getName.startsWith("tty."))
    .map(_.getName).toSeq
}
