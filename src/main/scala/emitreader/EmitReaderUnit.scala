package emitreader

import akka.actor._
import akka.util.ByteString
import concurrent.util.Duration
import java.util.concurrent.TimeUnit
import purejavacomm.{SerialPortEvent, SerialPortEventListener, SerialPort, CommPortIdentifier}
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
    import scala.collection.JavaConverters._
    val pid = CommPortIdentifier.getPortIdentifiers.asScala.map(_.asInstanceOf[CommPortIdentifier]).find(_.getName.startsWith(serialPortName)).getOrElse(sys.error("Serial port %s not found" format serialPortName))
    val port = pid.open(this.getClass.getName, 1000).asInstanceOf[SerialPort]
    port.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE)
    port.setFlowControlMode(SerialPort.FLOWCONTROL_XONXOFF_IN)
    port.notifyOnDataAvailable(true)
    port.addEventListener(new SerialPortEventListener() {
      def serialEvent(event: SerialPortEvent) {
        if (event.getEventType == SerialPortEvent.DATA_AVAILABLE) {
          val stream = port.getInputStream
          val expected = stream.available
          val buffer = new Array[Byte](expected)
          val actual = stream.read(buffer, 0, expected)
          handler ! IO.Chunk(ByteString.fromArray(buffer, 0, actual).map(b => (b ^ 0xDF.toByte).toByte))
        }
      }
    })
  }
}
