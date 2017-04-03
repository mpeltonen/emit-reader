package emitreader.sources.serialport

import akka.actor.ActorRef
import akka.util.ByteString
import emitreader.domain.EmitDataSource

class SerialPortSource(flowSourceActor: ActorRef, serialPortName: String) extends EmitDataSource {
  val port = new SerialPort(serialPortName, stream => {
    val buffer = new Array[Byte](1024)
    val actual = stream.read(buffer, 0, buffer.length)
    flowSourceActor ! ByteString.fromArray(buffer, 0, actual)
  })

  override def terminate(): Unit = {
    port.close()
  }
}
