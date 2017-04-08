package emitreader.domain

import akka.stream.scaladsl.Sink

trait EmitDataTarget[T] {
  def getEmitDataSink(): Sink[T, _]
  def terminate(): Unit = {}
}
