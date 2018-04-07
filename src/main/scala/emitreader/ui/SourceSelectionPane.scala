package emitreader.ui

import emitreader.config.Config
import emitreader.config.Config.Keys
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
    Config.setValue(Keys.SourceType, sourceTypeSelection.getSelectionModel.getSelectedIndex.toString)

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

  val storedSelection = Integer.parseInt(Config.getValue(Config.Keys.SourceType, "0"))
  sourceTypeSelection.selectionModel().select(storedSelection)
  sourceTypeSelection.disable <== viewModel.isStarted
}
