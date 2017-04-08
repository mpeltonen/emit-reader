package emitreader.domain

import akka.NotUsed
import akka.stream.{FlowShape, Graph}
import akka.util.ByteString
import emitreader.ui.ViewModel

import scalafx.scene.layout.Pane

trait EmitDataTargetType[T] {
  def getDecodeFlow(frameLen: Int): Graph[FlowShape[ByteString, T], NotUsed]

  val displayName: String

  def getUiPane(viewModel: ViewModel): Pane
  def startTarget(): EmitDataTarget[T]

  override def toString() = displayName
}
