package emitreader

import akka.actor.{ActorRef, ActorSystem}
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.util.ByteString

class EmitReaderUnit(serialPortName: String, frameLength: Int, callback: EmitCard => Unit) {
  implicit val system = ActorSystem("emit-reader")
  implicit val materializer = ActorMaterializer()

  val source: Source[ByteString, ActorRef] = Source.actorRef[ByteString](2048, OverflowStrategy.dropBuffer)
  val sink = Sink.foreach[EmitCard](card => callback(card))
  val flow = source.via(EmitEptFlow(frameLength)).to(sink)
  val actorRef = flow.run();
  val serialPort = startSerialPort(actorRef)

  def stop(): Unit = {
    serialPort.close()
    system.terminate()
  }

  private def startSerialPort(streamSource: ActorRef): SerialPort = {
    val port = new SerialPort(serialPortName)
    port.onDataAvailable { stream =>
      val buffer = new Array[Byte](1024)
      val actual = stream.read(buffer, 0, buffer.length)
      streamSource ! ByteString.fromArray(buffer, 0, actual)
    }
    port
  }
}
