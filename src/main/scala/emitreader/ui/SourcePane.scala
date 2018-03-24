package emitreader.ui

import emitreader.domain.EmitDataSourceType

import scalafx.scene.control.ComboBox

class SourcePane(viewModel: ViewModel) extends DefaultVBox {
  val title = new TitleLabel("Source")

  val sourceTypeSelection = new ComboBox[EmitDataSourceType](viewModel.sourceTypes) {
    maxWidth = Double.MaxValue
    viewModel.selectedSourceType <== selectionModel().selectedItemProperty()
  }

  minWidth = 270.0
  children = Seq(title, sourceTypeSelection)

  viewModel.selectedSourceType.onChange { (_, _, newSourceType) => {
    if (children.size() > 2) {
      children.remove(2)
    }
    children.add(newSourceType.getUiPane(viewModel))
  }}

  sourceTypeSelection.disable <== viewModel.isStarted

  sourceTypeSelection.selectionModel().selectFirst()
}
