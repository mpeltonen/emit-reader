package emitreader.sources.binaryfile

import akka.actor.ActorRef
import emitreader.domain.{EmitDataSource, EmitDataSourceType}
import emitreader.ui.ViewModel

import scalafx.scene.layout.Pane

class BinaryFileSourceType extends EmitDataSourceType {
  val localViewModel = new BinaryFileSourceViewModel()

  override val displayName = "File"

  override def startSource(actorRef: ActorRef): EmitDataSource = new BinaryFileSource(actorRef, localViewModel)

  override def getUiPane(viewModel: ViewModel): Pane = new BinaryFileSourceViewPane(viewModel, localViewModel)
}
