package emitreader

import akka.util.ByteString
import emitreader.Pipeline.EmitEptPipelineContext
import akka.io.{PipelineInjector, PipelineFactory}

class EmitReaderUnit(serialPortName: String, ctx: EmitEptPipelineContext, callback: ((ReadingTime, EmitCardId, Punches)) => Unit) {
  val pipeline = PipelineFactory.buildWithSinkFunctions(ctx, Pipeline.stages)(_ => (), e => callback(e.get))
  startSerialPort(pipeline)

  def startSerialPort(pipeline: PipelineInjector[Unit, ByteString]): Unit = {
    new SerialPort(serialPortName).onDataAvailable { stream =>
       val expected = stream.available
       val buffer = new Array[Byte](expected)
       val actual = stream.read(buffer, 0, expected)
       pipeline.injectEvent(ByteString.fromArray(buffer, 0, actual))
    }
  }
}
