package emitreader.ui

import emitreader.domain.DecoderType

import scalafx.beans.binding.Bindings.createBooleanBinding
import scalafx.geometry.Insets
import scalafx.scene.control.{Button, ComboBox}
import scalafx.scene.layout.VBox

class StartStopPane(controller: Controller, viewModel: ViewModel) extends DefaultVBox {
  val title = new TitleLabel("Readout type")

  val unitTypeSelection = new ComboBox[DecoderType](viewModel.decoderTypes) {
    maxWidth = Double.MaxValue
    viewModel.selectedDecoderType <== selectionModel().selectedItemProperty()
  }

  val buttonsPane = new VBox {
    val startBtn = new Button("START") {
      def shouldDisable = () => {
        Seq(
          viewModel.selectedDecoderType,
          viewModel.selectedSourceType,
          viewModel.selectedTargetType
        ).exists(m => Option(m.value).isEmpty) || viewModel.isStarted()
      }

      disable <== createBooleanBinding(shouldDisable, viewModel.selectedDecoderType, viewModel.selectedSourceType, viewModel.selectedTargetType, viewModel.isStarted)

      onAction = e => controller.onStartButtonClick()
    }

    val stopBtn = new Button("STOP") {
      onAction = e => controller.onStopButtonClick()
      disable <== createBooleanBinding(() => !viewModel.isStarted(), viewModel.isStarted)
    }

    padding = Insets(10,10,10,10)
    spacing = 10
    startBtn.maxWidth = Double.MaxValue
    stopBtn.maxWidth = Double.MaxValue
    startBtn.prefHeight = 50
    stopBtn.prefHeight = 50

    children = Seq(startBtn, stopBtn)
  }

  children = Seq(title, unitTypeSelection, buttonsPane)
  minWidth = 170.0

  unitTypeSelection.selectionModel().select(viewModel.decoderTypes(0))
}
