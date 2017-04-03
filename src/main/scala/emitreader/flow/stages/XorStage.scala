package emitreader.flow.stages

import akka.stream.Attributes
import akka.util.ByteString

class XorStage extends EmitEptFlowStage[ByteString, ByteString] {
  override def createLogic(inheritedAttributes: Attributes) = new EmitEptFlowStageLogic(shape) {
    override def onPush(): Unit = {
      push(out, grab(in).map(b => (b ^ 0xDF.toByte).toByte))
      pull(in)
    }
  }
}
