package emitreader.flow.stages

import akka.stream.Attributes
import akka.util.ByteString

class FramingStage(frameLen: Int) extends EmitEptFlowStage[ByteString, ByteString] {
  override def createLogic(inheritedAttributes: Attributes) = new EmitEptFlowStageLogic(shape) {
    var buffer: ByteString = ByteString.empty

    def findFrame(data: ByteString): Option[ByteString] = {
      val frameStartMarker = ByteString(0xFF.toByte, 0xFF.toByte)
      val frameStartIndex = data.indexOfSlice(frameStartMarker)
      if (frameStartIndex >= 0 && frameStartIndex + frameLen <= data.length) {
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
