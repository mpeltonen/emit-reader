package emitreader.sources.emulator

import akka.actor.ActorRef
import emitreader.domain.{EmitDataSource, EmitDataSourceType}
import emitreader.ui.{DefaultVBox, ViewModel}

import scalafx.scene.layout.Pane

class EmulatorEmitDataSourceType extends EmitDataSourceType {
  val localViewModel = new EmulatorEmitDataSourceViewModel()

  override val displayName = "Emulator"

  override def startSource(actorRef: ActorRef): EmitDataSource = new EmulatorEmitDataSource(actorRef, localViewModel)

  override def getUiPane(viewModel: ViewModel): Pane = new EmulatorEmitDataSourceViewPane(viewModel, localViewModel)
}
