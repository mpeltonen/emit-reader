package emitreader.targets.uitable

import akka.stream.scaladsl.Sink
import emitreader.domain.{EmitData, EmitDataTarget}

class UiTableTarget(localViewModel: UiTableTargetViewModel) extends EmitDataTarget[EmitData] {
  var lastEmitCard = EmitData(0, 0, Seq())

  override def getEmitDataSink(): Sink[EmitData, _] = Sink.foreach(emitCard => {
    if (lastEmitCard.cardId != emitCard.cardId) {
      localViewModel.tableViewModel.append(emitCard)
      lastEmitCard = emitCard
    }
  })
}
