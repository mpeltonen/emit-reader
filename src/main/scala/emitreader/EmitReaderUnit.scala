package emitreader

import akka.actor.{ActorRef, ActorSystem}
import akka.util.ByteString
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.stream.scaladsl.{Sink, Source}

class EmitReaderUnit(serialPortName: String, frameLength: Int, callback: PunchCardData => Unit) {
  implicit val system = ActorSystem("emit-reader")
  implicit val materializer = ActorMaterializer()
  val source: Source[ByteString, ActorRef] = Source.actorRef[ByteString](2048, OverflowStrategy.dropBuffer)
  val sink = Sink.foreach[PunchCardData](pcd => callback(pcd))
  val flow = source.via(EmitEptFlow(frameLength)).to(sink)
  startSerialPort(flow.run())

  def startSerialPort(streamSource: ActorRef): Unit = {
    new SerialPort(serialPortName).onDataAvailable { stream =>
      val buffer = new Array[Byte](1024)
      val actual = stream.read(buffer, 0, buffer.length)
      streamSource ! ByteString.fromArray(buffer, 0, actual)
    }
  }
}
