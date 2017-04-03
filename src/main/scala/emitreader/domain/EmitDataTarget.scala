package emitreader.domain

import akka.stream.scaladsl.Sink

trait EmitDataTarget {
  def getEmitDataSink(): Sink[EmitCard, _]
  def terminate()
}
