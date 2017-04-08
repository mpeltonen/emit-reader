package emitreader.targets.uitable

import akka.NotUsed
import akka.stream.{FlowShape, Graph}
import akka.util.ByteString
import emitreader.domain.{EmitCard, EmitDataTarget, EmitDataTargetType}
import emitreader.flow.EmitEptDecodeFlow
import emitreader.ui.ViewModel

import scalafx.scene.layout.Pane

class UiTableTargetType extends EmitDataTargetType[EmitCard] {
  val localViewModel = new UiTableTargetViewModel()

  override val displayName = "UI Table"

  override def getUiPane(viewModel: ViewModel): Pane = new UiTableTargetViewPane(viewModel, localViewModel)

  override def startTarget(): EmitDataTarget[EmitCard] = new UiTableTarget(localViewModel)

  override def getDecodeFlow(frameLen: Int): Graph[FlowShape[ByteString, EmitCard], NotUsed] = EmitEptDecodeFlow(frameLen)
}
