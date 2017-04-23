package emitreader.targets.rogainmanager

import java.net.InetSocketAddress

import akka.actor.ActorSystem
import akka.stream.scaladsl.Sink
import emitreader.domain.{EmitData, EmitDataTarget}

class RogainManagerTarget(localViewModel: RogainManagerTargetViewModel) extends EmitDataTarget[EmitData] {
  var lastEmitCard = EmitData(0, 0, Seq())
  implicit val system = ActorSystem("rogain-manager")

  val logger = system.actorOf(RogainManagerUiLogger.props(localViewModel.logBuffer))
  val rogainManagerListener = system.actorOf(RogainManagerListener.props(logger))
  val rogainManagerClient = system.actorOf(RogainManagerClient.props(new InetSocketAddress(localViewModel.ipAddress(), 6001), rogainManagerListener, logger))

  override def getEmitDataSink(): Sink[EmitData, _] = Sink.foreach(emitCard => {
    if (lastEmitCard.cardId != emitCard.cardId) {

    }
  })

  override def terminate(): Unit = system.terminate()
}
