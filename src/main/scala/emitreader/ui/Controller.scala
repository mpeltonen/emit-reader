package emitreader.ui

import emitreader.domain._
import emitreader.flow.EmitReaderFlow

class Controller(val model: ViewModel) {
  var emitReaderFlow: Option[EmitReaderFlow] = None

  def onStartButtonClick(): Unit = {
    val sourceType: EmitDataSourceType = model.selectedSourceType()
    val targetType: EmitDataTargetType = model.selectedTargetType()
    val decodeType: DecoderType = model.selectedDecoderType()

    emitReaderFlow = Some(new EmitReaderFlow(sourceType, decodeType, targetType))
    model.isStarted.setValue(true)
  }

  def onStopButtonClick(): Unit = {
    emitReaderFlow.foreach(_.terminate())
    model.isStarted.setValue(false)
  }

  model.appQuitRequested.onChange((_, _, _) => {
    emitReaderFlow.foreach(_.terminate())
  })

}
