package emitreader

import akka.actor._
import akka.util.ByteString
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit

class EmitReaderUnit(serialPortName: String, callback: (Long, Int, Seq[(Int, Int)]) => Unit, decoder: => IO.Iteratee[(Long, Int, Seq[(Int, Int)])] = Decoder.full) {
  val system = ActorSystem("%s-%s".format(getClass.getSimpleName, serialPortName).replace('.', '-'))
  val handler = system.actorOf(Props(new Handler))
  var iterateeState: IO.IterateeRefSync[(Long, Int, Seq[(Int, Int)])] = new IO.IterateeRefSync(decoder)
  startSerialPort(handler)

  private class Handler extends Actor {
    def receive = {
      case chunk: IO.Chunk => {
        iterateeState apply chunk
        context.setReceiveTimeout(Duration(30, TimeUnit.MILLISECONDS))
      }
      case ReceiveTimeout => {
        context.setReceiveTimeout(Duration.Undefined)
        iterateeState apply IO.EOF
        iterateeState.value match {
          case (IO.Done((readTime, emitId, punches)), _) => {
            callback(readTime, emitId, punches)
          }
          case _ =>
        }
        iterateeState = new IO.IterateeRefSync(decoder)
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
