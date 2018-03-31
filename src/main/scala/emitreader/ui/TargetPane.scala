package emitreader.ui

import emitreader.domain.EmitDataTargetType

import scalafx.scene.control.ComboBox
import scalafx.scene.layout.{Priority, VBox}

class TargetPane(viewModel: ViewModel) extends VBox {
  val title = new TitleLabel("Target")

  val targetTypeSelection = new ComboBox[EmitDataTargetType[_]](viewModel.targetTypes) {
    maxWidth = Double.MaxValue
    viewModel.selectedTargetType <== selectionModel().selectedItemProperty()
  }

  children = Seq(title, targetTypeSelection)

  viewModel.selectedTargetType.onChange { (_, _, newTargetType) => {
    val targetUiPane = newTargetType.getUiPane(viewModel)
    VBox.setVgrow(targetUiPane, Priority.Always)
    if (children.size() > 2) {
      children.remove(2)
    }
    children.add(targetUiPane)
  }}

  targetTypeSelection.disable <== viewModel.isStarted

  targetTypeSelection.selectionModel().selectFirst()
}
