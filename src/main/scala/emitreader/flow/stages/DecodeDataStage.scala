package emitreader.flow.stages

import java.nio.ByteOrder

import akka.stream.Attributes
import akka.util.{ByteIterator, ByteString}
import emitreader.domain.{EmitCard, Punch}

class DecodeDataStage(frameLen: Int) extends EmitEptFlowStage[ByteString, EmitCard] {
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

      push(out, EmitCard(cardId, System.currentTimeMillis(), controlData))
      pull(in)
    }
  }
}
