package emitreader.sources.serialport

import akka.actor.ActorRef
import emitreader.domain.{EmitDataSource, EmitDataSourceType}
import emitreader.ui.ViewModel

import scalafx.scene.layout.Pane

class SerialPortSourceType extends EmitDataSourceType {
  val localViewModel = new SerialPortSourceViewModel()

  override val displayName = "Serial port"

  override def startSource(actorRef: ActorRef): EmitDataSource = {
    new SerialPortSource(actorRef, localViewModel.selectedSerialPortName())
  }

  override def getUiPane(viewModel: ViewModel): Pane = new SerialPortSourceViewPane(viewModel, localViewModel)
}
