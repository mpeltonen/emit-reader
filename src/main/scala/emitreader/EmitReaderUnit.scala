package emitreader

import akka.actor._
import akka.util.ByteString
import concurrent.util.Duration
import java.util.concurrent.TimeUnit
import purejavacomm.{SerialPortEvent, SerialPortEventListener, SerialPort, CommPortIdentifier}
import java.nio.ByteOrder

class EmitReaderUnit(serialPortName: String, callback: (Long, Int, Seq[(Int, Int)]) => Unit) {
  val system = ActorSystem("%s-%s".format(getClass.getSimpleName, serialPortName).replace('.', '-'))
  val handler = system.actorOf(Props(new Handler))
  val state = IO.IterateeRef.Map.sync[String]()
  startSerialPort(handler)

  private class Handler extends Actor {
    def receive = {
      case chunk: IO.Chunk => {
        state(serialPortName) flatMap (_ => decoder)
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

  def decoder: IO.Iteratee[Unit] = {
    for {
      prefix <- IO.takeUntil(ByteString(0xFF.toByte, 0xFF.toByte), true)
      id <- IO.take(3)
      unused1 <- IO.take(1)
      cardProductionWeek <- IO.take(1)
      cardProductionYear <- IO.take(1)
      unused2 <- IO.take(1)
      checksum1 <- IO.take(1)
      punchingData <- IO.take(150)
      unused3 <- IO.take(56)
      checksum2 <- IO.take(1)
    } yield {
      val allBytes = prefix ++ id ++ unused1 ++ cardProductionWeek ++ cardProductionYear ++ unused2 ++ checksum1 ++ punchingData ++ unused3 ++ checksum2
      val checksum = allBytes.sum % 256
      val validPacket = allBytes.nonEmpty && checksum == 0
      if (validPacket) {
        def unsigned(b: Byte) = 0xff & b
        val cardId = (unsigned(id(0)) | unsigned(id(1)) << 8 | unsigned(id(2)) << 16)
        val iterator = punchingData.iterator
        val punches = for (i <- 0 until 50) yield (unsigned(iterator.getByte), iterator.getShort(ByteOrder.LITTLE_ENDIAN).toInt)
        callback(System.currentTimeMillis, cardId, punches)
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
