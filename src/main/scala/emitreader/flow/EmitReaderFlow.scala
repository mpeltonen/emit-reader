package emitreader.flow

import akka.actor.{ActorRef, ActorSystem}
import akka.stream.scaladsl.{RunnableGraph, Sink, Source}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.util.ByteString
import emitreader.domain._

class EmitReaderFlow[T](sourceType: EmitDataSourceType, decoderType: DecoderType, targetType: EmitDataTargetType[T]) {
  implicit val system = ActorSystem("emit-reader")
  implicit val materializer = ActorMaterializer()

  val emitDataTarget = targetType.startTarget()
  val sink: Sink[T, _] = emitDataTarget.getEmitDataSink()
  val source: Source[ByteString, ActorRef] = Source.actorRef[ByteString](2048, OverflowStrategy.dropBuffer)
  val flow: RunnableGraph[ActorRef] = source.via(targetType.getDecodeFlow(decoderType.frameLen)).to(sink)
  val actorRef: ActorRef = flow.run()
  val emitDataSource: EmitDataSource = sourceType.startSource(actorRef)

  def terminate(): Unit = {
    emitDataSource.terminate()
    emitDataTarget.terminate()
    system.terminate()
  }
}
