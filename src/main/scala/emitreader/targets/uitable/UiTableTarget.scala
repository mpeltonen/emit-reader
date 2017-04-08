package emitreader.targets.uitable

import akka.stream.scaladsl.Sink
import emitreader.domain.{EmitCard, EmitDataTarget}

class UiTableTarget(localViewModel: UiTableTargetViewModel) extends EmitDataTarget[EmitCard] {
  var lastEmitCard = EmitCard(0, 0, Seq())

  override def getEmitDataSink(): Sink[EmitCard, _] = Sink.foreach(emitCard => {
    if (lastEmitCard.cardId != emitCard.cardId) {
      localViewModel.tableViewModel.append(emitCard)
      lastEmitCard = emitCard
    }
  })

  override def terminate(): Unit = {}
}
