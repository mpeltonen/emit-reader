package emitreader.targets.binaryfile

import akka.NotUsed
import akka.stream.{FlowShape, Graph}
import akka.util.ByteString
import emitreader.domain.{EmitDataTarget, EmitDataTargetType}
import emitreader.flow.EmitEptByteFrameFlow
import emitreader.ui.ViewModel

import scalafx.scene.layout.Pane

class BinaryFileTargetType extends EmitDataTargetType[ByteString] {
  val localViewModel = new BinaryFileTargetViewModel()

  override val displayName = "File"

  override def getUiPane(viewModel: ViewModel): Pane = new BinaryFileTargetViewPane(viewModel, localViewModel)

  override def startTarget(): EmitDataTarget[ByteString] = new BinaryFileTarget(localViewModel)

  override def getDecodeFlow(frameLen: Int): Graph[FlowShape[ByteString, ByteString], NotUsed] = EmitEptByteFrameFlow(frameLen)
}
