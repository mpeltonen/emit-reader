package emitreader

import java.nio.ByteOrder

import akka.stream.stage.{GraphStage, GraphStageLogicWithLogging, InHandler, OutHandler}
import akka.stream.{Attributes, FlowShape, Inlet, Outlet}
import akka.util.{ByteIterator, ByteString}
import akka.stream.scaladsl.GraphDSL
import akka.stream.scaladsl.GraphDSL.Implicits._

object EmitEptFlow {
  def apply(frameLen: Int) = {
    GraphDSL.create() { implicit builder =>
      val A = builder.add(new XorStage())
      val B = builder.add(new FramingStage(frameLen))
      val C = builder.add(new ChecksumCheckStage(frameLen))
      val D = builder.add(new DecodeDataStage(frameLen))

      A ~> B ~> C ~> D
      FlowShape(A.in, D.out)
    }.named(EmitEptFlow.getClass.getSimpleName)
  }
}

private class XorStage extends EmitEptFlowStage[ByteString, ByteString] {
  override def createLogic(inheritedAttributes: Attributes) = new EmitEptFlowStageLogic(shape) {
    override def onPush(): Unit = {
      push(out, grab(in).map(b => (b ^ 0xDF.toByte).toByte))
      pull(in)
    }
  }
}

private class FramingStage(frameLen: Int) extends EmitEptFlowStage[ByteString, ByteString] {
  override def createLogic(inheritedAttributes: Attributes) = new EmitEptFlowStageLogic(shape) {
    var buffer: ByteString = ByteString.empty

    def findFrame(data: ByteString): Option[ByteString] = {
      val frameStartMarker = ByteString(0xFF.toByte, 0xFF.toByte)
      val frameStartIndex = data.indexOfSlice(frameStartMarker)
      if (frameStartIndex >= 0 && frameStartIndex + frameLen < data.length) {
        return Some(data.slice(frameStartIndex, frameStartIndex + frameLen))
      } else {
        None
      }
    }

    override def onPush(): Unit = {
      buffer = buffer ++ grab(in)
      findFrame(buffer).foreach(frame => {
        push(out, frame)
        buffer = ByteString.empty
      })
      pull(in)
    }
  }
}

private class ChecksumCheckStage(frameLen: Int) extends EmitEptFlowStage[ByteString, ByteString] {
  override def createLogic(inheritedAttributes: Attributes) = new EmitEptFlowStageLogic(shape) {
    def checksumBytes(frame: ByteString) = if (frameLen == 10) frame.drop(2) else frame
    def checksumOk(frame: ByteString): Boolean = frame.nonEmpty && checksumBytes(frame).sum % 256 == 0

    override def onPush(): Unit = {
      val frame = grab(in)
      if (checksumOk(frame)) {
        push(out, frame)
      } else {
        log.warning("Dropping frame with invalid checksum")
      }
      pull(in)
    }
  }
}

private class DecodeDataStage(frameLen: Int) extends EmitEptFlowStage[ByteString, PunchCardData] {
  override def createLogic(attr: Attributes) = new EmitEptFlowStageLogic(shape) {
    implicit val byteOrder = ByteOrder.LITTLE_ENDIAN
    @inline def isLowBatteryMarkerCode(code: Int) = code == 99
    @inline def isLowBatteryMarkerPunch(punch: Seq[Byte]) = isLowBatteryMarkerCode(unsigned(punch(0)))
    @inline def unsigned(b: Byte) = b & 0xFF

    private def decodeControlData(bytes: ByteIterator): Seq[Punch] = {
      val punches = bytes.sliding(4, step = 3).take(50).foldLeft(Seq[Punch]()) { (acc, punch) => {
        if (!isLowBatteryMarkerPunch(punch)) {
          val (controlCode, punchTime) = (unsigned(punch(0)), (unsigned(punch(1)) | unsigned(punch(2)) << 8).toLong)
          val isLowBattery = (acc.length < 50 && isLowBatteryMarkerCode(unsigned(punch(3))))
          acc :+ Punch(controlCode, punchTime, isLowBattery)
        } else acc
      }}
      punches.reverse.dropWhile(_.controlCode == 0).reverse
    }

    override def onPush(): Unit = {
      val frame: ByteIterator = grab(in).iterator
      // See http://ttime.no/rs232.pdf (last page) for protocol description
      frame.drop(2)
      val cardId = frame.getLongPart(3)
      frame.drop(5)
      val controlData = if (frameLen > 10) decodeControlData(frame) else Seq.empty

      push(out, PunchCardData(System.currentTimeMillis(), cardId, controlData))
      pull(in)
    }
  }
}

private abstract class EmitEptFlowStage[IN, OUT] extends GraphStage[FlowShape[IN, OUT]] {
  val in = Inlet[IN](s"${this.getClass.getSimpleName}.in")
  val out = Outlet[OUT](s"${this.getClass.getSimpleName}.out")

  override val shape = FlowShape.of(in, out)

  protected abstract class EmitEptFlowStageLogic(shape: FlowShape[IN, OUT]) extends GraphStageLogicWithLogging(shape) with InHandler with OutHandler {
    override def onPush(): Unit

    override def onPull(): Unit = {
      if (!hasBeenPulled(in)) pull(in)
    }

    setHandlers(in, out, this)
  }
}
