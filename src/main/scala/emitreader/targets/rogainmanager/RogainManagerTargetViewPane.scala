package emitreader.targets.rogainmanager

import emitreader.ui.{DefaultHBox, DefaultVBox, ViewModel}

import scalafx.geometry.Pos
import scalafx.scene.control.{Label, ListView, TextField}
import scalafx.scene.layout.Priority

class RogainManagerTargetViewPane(globalViewModel: ViewModel, localViewModel: RogainManagerTargetViewModel) extends DefaultVBox {
  val ipAddressPane = new DefaultHBox {
    val ipAddress = new TextField() {
      text <==> localViewModel.ipAddress

    }

    alignment = Pos.BaselineLeft
    children = Seq(new Label("IP Address"), ipAddress)
  }

  val logView = new ListView[CharSequence](localViewModel.logBuffer) {
    this.delegate.setStyle("-fx-font: 12px \"Courier\";")
    prefWidth = 600
    maxWidth = Double.MaxValue
  }

  scalafx.scene.layout.VBox.setVgrow(logView, Priority.Always)

  localViewModel.logBuffer.onChange((buf, _) => {
    logView.scrollTo(Math.max(0, buf.size - 1))
  })

  children = Seq(ipAddressPane, logView)
}
