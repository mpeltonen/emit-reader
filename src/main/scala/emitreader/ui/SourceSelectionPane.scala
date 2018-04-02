package emitreader.ui

import emitreader.domain.EmitDataSourceType

import scalafx.scene.control.ComboBox
import scalafx.scene.layout.VBox

class SourceSelectionPane(viewModel: ViewModel) extends VBox {
  styleClass += "source-selection-pane"

  val sourceTypeSelection = new ComboBox[EmitDataSourceType](viewModel.sourceTypes) {
    maxWidth = Double.MaxValue
    viewModel.selectedSourceType <== selectionModel().selectedItemProperty()
  }

  minWidth = 270.0

  viewModel.selectedSourceType.onChange { (_, _, newSourceType) => {
    if (children.size() > 2) {
      children.remove(2)
    }

    val selectedSourceUiPane = newSourceType.getUiPane(viewModel)
    selectedSourceUiPane.styleClass += "source-ui-pane"
    children.add(selectedSourceUiPane)
  }}

  children = Seq(
    new TitleLabel("Source"),
    sourceTypeSelection
  )

  sourceTypeSelection.disable <== viewModel.isStarted
  sourceTypeSelection.selectionModel().selectFirst()
}
