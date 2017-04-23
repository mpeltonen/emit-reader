package emitreader.targets.rogainmanager

import akka.NotUsed
import akka.stream.{FlowShape, Graph}
import akka.util.ByteString
import emitreader.domain.{EmitData, EmitDataTarget, EmitDataTargetType}
import emitreader.flow.EmitEptDecodeFlow
import emitreader.ui.ViewModel

import scalafx.scene.layout.Pane

class RogainManagerTargetType extends EmitDataTargetType[EmitData] {
  val localViewModel = new RogainManagerTargetViewModel()

  override val displayName = "Tak-Soft Rogain Manager"

  override def getUiPane(viewModel: ViewModel): Pane = new RogainManagerTargetViewPane(viewModel, localViewModel)

  override def startTarget(): EmitDataTarget[EmitData] = new RogainManagerTarget(localViewModel)

  override def getDecodeFlow(frameLen: Int): Graph[FlowShape[ByteString, EmitData], NotUsed] = EmitEptDecodeFlow(frameLen)
}
