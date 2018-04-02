package emitreader.ui

import emitreader.domain.EmitDataTargetType

import scalafx.scene.control.ComboBox
import scalafx.scene.layout.{Priority, VBox}

class TargetSelectionPane(viewModel: ViewModel) extends VBox {
  styleClass += "target-selection-pane"

  val targetTypeSelection = new ComboBox[EmitDataTargetType[_]](viewModel.targetTypes) {
    maxWidth = Double.MaxValue
    viewModel.selectedTargetType <== selectionModel().selectedItemProperty()
  }

  children = Seq(new TitleLabel("Target"), targetTypeSelection)

  viewModel.selectedTargetType.onChange { (_, _, newTargetType) => {
    if (children.size() > 2) {
      children.remove(2)
    }

    val selectedTargetUiPane = newTargetType.getUiPane(viewModel)
    selectedTargetUiPane.styleClass += "target-ui-pane"
    VBox.setVgrow(selectedTargetUiPane, Priority.Always)

    children.add(selectedTargetUiPane)
  }}

  targetTypeSelection.disable <== viewModel.isStarted

  targetTypeSelection.selectionModel().selectFirst()
}
