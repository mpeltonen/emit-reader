package emitreader

import java.nio.ByteOrder

import akka.stream.stage.{GraphStage, GraphStageLogicWithLogging, InHandler, OutHandler}
import akka.stream.{Attributes, FlowShape, Inlet, Outlet}
import akka.util.{ByteIterator, ByteString}

class XorStage extends EmitEptFlowStage[ByteString, ByteString] {
  override def createLogic(inheritedAttributes: Attributes) = new EmitEptFlowStageLogic(shape) {
    override def onPush(): Unit = {
      push(out, grab(in).map(b => (b ^ 0xDF.toByte).toByte))
      pull(in)
    }
  }
}

class FramingStage(frameLen: Int) extends EmitEptFlowStage[ByteString, ByteString] {
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

class ChecksumCheckStage(frameLen: Int) extends EmitEptFlowStage[ByteString, ByteString] {
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

class DecodeDataStage(frameLen: Int) extends EmitEptFlowStage[ByteString, (ReadingTime, EmitCardId, Punches)] {
  override def createLogic(attr: Attributes) = new EmitEptFlowStageLogic(shape) {
    implicit val byteOrder = ByteOrder.LITTLE_ENDIAN
    @inline def isLowBattery(code: ControlCode) = code == 99
    @inline def unsigned(b: Byte) = b & 0xFF

    private def decodeControlData(bytes: ByteIterator): Seq[(ControlCode, SplitTime, LowBattery)] = {
      bytes.grouped(3).take(50).foldLeft((Seq[(ControlCode, SplitTime, LowBattery)](), false)) { (acc, ctrlData) =>
        val (controlCode, punchTime) = (unsigned(ctrlData(0)), (unsigned(ctrlData(1)) | unsigned(ctrlData(2)) << 8).toLong)
        if (isLowBattery(controlCode)) (acc._1, true)
        else (acc._1 :+ (controlCode, punchTime, acc._2), false)
      }._1
    }

    override def onPush(): Unit = {
      val frame: ByteIterator = grab(in).iterator
      // See http://ttime.no/rs232.pdf (last page) for protocol description
      frame.drop(2)
      val cardId = frame.getLongPart(3)
      frame.drop(5)
      val controlData = if (frameLen > 10) decodeControlData(frame) else Seq.empty

      push(out, (System.currentTimeMillis(), cardId, controlData))
      pull(in)
    }
  }
}

abstract class EmitEptFlowStage[IN, OUT] extends GraphStage[FlowShape[IN, OUT]] {
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
