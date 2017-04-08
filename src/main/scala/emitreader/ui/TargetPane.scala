package emitreader.ui

import emitreader.domain.EmitDataTargetType

import scalafx.scene.control.ComboBox
import scalafx.scene.layout.Priority

class TargetPane(viewModel: ViewModel) extends DefaultVBox {
  val title = new TitleLabel("Target")

  val targetTypeSelection = new ComboBox[EmitDataTargetType[_]](viewModel.targetTypes) {
    maxWidth = Double.MaxValue
    viewModel.selectedTargetType <== selectionModel().selectedItemProperty()
  }

  children = Seq(title, targetTypeSelection)

  viewModel.selectedTargetType.onChange { (_, _, newTargetType) => {
    val targetUiPane = newTargetType.getUiPane(viewModel)
    scalafx.scene.layout.VBox.setVgrow(targetUiPane, Priority.Always)
    if (children.size() > 2) {
      children.remove(2)
    }
    children.add(targetUiPane)
  }}

  targetTypeSelection.selectionModel().selectFirst()
}
