package emitreader

import purejavacomm.CommPortIdentifier
import purejavacomm.CommPortIdentifier.getPortIdentifiers

import scala.collection.JavaConverters._
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.event.ActionEvent
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.{ComboBox, Label, RadioButton, ToggleGroup}
import scalafx.scene.layout.VBox

object EmitReaderApp extends JFXApp {
  stage = new PrimaryStage {
    var emitReader: Option[EmitReaderUnit] = None
    title = "Emit Reader"

    scene = new Scene {
      val serialPortLabel = new Label("Serial port:")
      val serialPortSelection = new ComboBox[String](getSerialPortNames()) {
        onAction = (e: ActionEvent) =>  {
          val serialPortName = selectionModel().getSelectedItem
          emitReader.foreach(_.stop())
          val frameLen = if (type250.selected()) 217 else 10
          emitReader = Some(new EmitReaderUnit(serialPortName, frameLen, { (data: EmitCard) => println(data) }))
        }
      }

      val typeLabel = new Label("Unit type:")
      val type250 = new RadioButton("250") { selected = true }
      val typeOnline = new RadioButton("Online")
      val eptUnitTypeGroup = new ToggleGroup() {
        toggles = List(type250, typeOnline)
      }


      root = new VBox {
        padding = Insets(5)
        spacing = 5
        children = Seq(serialPortLabel, serialPortSelection, typeLabel, type250, typeOnline)
      }
    }
    onCloseRequest = handle {
      emitReader.foreach(_.stop())
    }
  }

  def getSerialPortNames(): Seq[String] = getPortIdentifiers().asScala
    .filter(_.getPortType() == CommPortIdentifier.PORT_SERIAL)
    .map(_.getName)
    .filter(_.startsWith("cu")).toSeq
}
