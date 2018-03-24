package emitreader.ui

import emitreader.domain._
import emitreader.flow.EmitDataFlow

class Controller(val model: ViewModel) {
  var emitDataFlow: Option[EmitDataFlow[_]] = None

  def onStartButtonClick(): Unit = {
    val sourceType: EmitDataSourceType = model.selectedSourceType()
    val targetType: EmitDataTargetType[_] = model.selectedTargetType()
    val decodeType: DecoderType = model.selectedDecoderType()

    emitDataFlow = Some(new EmitDataFlow(sourceType, decodeType, targetType))
    model.isStarted() = true
  }

  def onStopButtonClick(): Unit = {
    emitDataFlow.foreach(_.terminate())
    model.isStarted() = false
    emitDataFlow = None
  }

  model.appQuitRequested.onChange((_, _, _) => {
    emitDataFlow.foreach(_.terminate())
  })

}
