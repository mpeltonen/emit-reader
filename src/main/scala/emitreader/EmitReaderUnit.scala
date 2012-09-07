package emitreader

import akka.actor._
import akka.util.ByteString
import concurrent.util.Duration
import java.util.concurrent.TimeUnit
import akka.actor.IO.Iteratee

class EmitReaderUnit(serialPortName: String, callback: (Long, Int, Seq[(Int, Int)]) => Unit, decoder: ((Long, Int, Seq[(Int, Int)]) => Unit) => Iteratee[Unit] = Decoder.full) {
  val system = ActorSystem("%s-%s".format(getClass.getSimpleName, serialPortName).replace('.', '-'))
  val handler = system.actorOf(Props(new Handler))
  val state = IO.IterateeRef.Map.sync[String]()
  startSerialPort(handler)

  private class Handler extends Actor {
    def receive = {
      case chunk: IO.Chunk => {
        state(serialPortName) flatMap (_ => decoder(callback))
        state(serialPortName) apply chunk
        context.setReceiveTimeout(Duration(30, TimeUnit.MILLISECONDS))
      }
      case ReceiveTimeout => {
        context.resetReceiveTimeout()
        state(serialPortName) apply IO.EOF
        state -= serialPortName
      }
    }
  }

  def startSerialPort(handler: ActorRef): Unit = {
    new SerialPort(serialPortName).onDataAvailable { stream =>
       val expected = stream.available
       val buffer = new Array[Byte](expected)
       val actual = stream.read(buffer, 0, expected)
       handler ! IO.Chunk(ByteString.fromArray(buffer, 0, actual).map(b => (b ^ 0xDF.toByte).toByte))
    }
  }
}
