package emitreader.flow.stages

import akka.stream.{FlowShape, Inlet, Outlet}
import akka.stream.stage.{GraphStage, GraphStageLogicWithLogging, InHandler, OutHandler}

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
