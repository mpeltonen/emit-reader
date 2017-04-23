package emitreader.targets.rogainmanager

import emitreader.ui.{DefaultHBox, DefaultVBox, ViewModel}

import scalafx.geometry.Pos
import scalafx.scene.control.{Label, ListView, TextField}

class RogainManagerTargetViewPane(globalViewModel: ViewModel, localViewModel: RogainManagerTargetViewModel) extends DefaultVBox {
  val ipAddressPane = new DefaultHBox {
    val ipAddress = new TextField() {
      text <==> localViewModel.ipAddress

    }

    alignment = Pos.BaselineLeft
    children = Seq(new Label("IP Address"), ipAddress)
  }

  val logView = new ListView[CharSequence](localViewModel.logBuffer) {
    this.setStyle("-fx-font: 12px \"Courier\";")


  }
  localViewModel.logBuffer.onChange((buf, _) => {
    logView.scrollTo(Math.max(0, buf.size - 1))
  })

  children = Seq(ipAddressPane, logView)
}
