package emitreader.domain

import akka.actor.ActorRef
import emitreader.ui.ViewModel

import scalafx.scene.layout.Pane

trait EmitDataSourceType {
  val displayName: String

  def getUiPane(viewModel: ViewModel): Pane
  def startSource(flowSourceActor: ActorRef): EmitDataSource

  override def toString() = displayName
}
