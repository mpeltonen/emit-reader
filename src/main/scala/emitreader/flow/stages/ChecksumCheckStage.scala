package emitreader.flow.stages

import akka.stream.Attributes
import akka.util.ByteString

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
