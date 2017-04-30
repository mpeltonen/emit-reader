package emitreader.targets.rogainmanager

import java.net.InetSocketAddress

import akka.actor.ActorSystem
import akka.stream.scaladsl.Sink
import akka.util.ByteString
import emitreader.domain.{EmitData, EmitDataTarget}

class RogainManagerTarget(localViewModel: RogainManagerTargetViewModel) extends EmitDataTarget[EmitData] {
  var lastEmitCard = EmitData(0, 0, Seq())
  implicit val system = ActorSystem("rogain-manager")

  val logger = system.actorOf(RogainManagerUiLogger.props(localViewModel.logBuffer))
  val rogainManagerListener = system.actorOf(RogainManagerListener.props(responseHandler))
  val rogainManagerClient = system.actorOf(RogainManagerClient.props(new InetSocketAddress(localViewModel.ipAddress(), 6001), rogainManagerListener, logger))
  val rogainManagerProtocol = new RogainManagerProtocol(logger)

  def responseHandler(bs: ByteString): Unit = {
    rogainManagerProtocol.responseHandler(bs).foreach(command => rogainManagerClient ! command)
  }

  override def getEmitDataSink(): Sink[EmitData, _] = Sink.foreach(emitData => {
    if (lastEmitCard.cardId != emitData.cardId) {
      rogainManagerClient ! rogainManagerProtocol.toSendDataCommand(emitData)
    }
  })

  override def terminate(): Unit = system.terminate()
}
