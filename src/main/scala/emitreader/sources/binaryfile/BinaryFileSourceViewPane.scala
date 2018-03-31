package emitreader.sources.binaryfile

import emitreader.ui.ViewModel

import scalafx.beans.binding.Bindings.createBooleanBinding
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.VBox
import scalafx.stage.FileChooser
import scalafx.stage.FileChooser.ExtensionFilter

class BinaryFileSourceViewPane(globalViewModel: ViewModel, localViewModel: BinaryFileSourceViewModel) extends VBox {
  val fileChooser = new FileChooser {
    title = "Open Emit Data File"
    extensionFilters.addAll(new ExtensionFilter("Emit Files", "*.bin"))
  }

  val chooseFileButton = new Button("Choose File") {
    onAction = e => {
      val file = fileChooser.showOpenDialog(scene().getWindow)
      localViewModel.fileName.setValue(Option(file).map(_.getAbsolutePath).getOrElse(""))
    }
  }

  val fileNameLabel = new Label() {
    text <== localViewModel.fileName
  }

  val sendDataButton = new Button("Send Data") {
    onAction = e => {
      localViewModel.sendDataClickCount.setValue(localViewModel.sendDataClickCount() + 1)
    }
    disable <== createBooleanBinding(
      () => Option(localViewModel.fileName()).isEmpty,
      localViewModel.fileName
    )
  }

  children = Seq(chooseFileButton, fileNameLabel, sendDataButton)
}
