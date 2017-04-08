package emitreader.flow

import akka.NotUsed
import akka.stream.scaladsl.GraphDSL
import akka.stream.scaladsl.GraphDSL.Implicits._
import akka.stream.{FlowShape, Graph}
import akka.util.ByteString
import emitreader.flow.stages.{ChecksumCheckStage, FramingStage, XorStage}

object EmitEptByteFrameFlow {
  def apply(frameLen: Int): Graph[FlowShape[ByteString, ByteString], NotUsed] = {
    GraphDSL.create() { implicit builder =>
      val A = builder.add(new XorStage())
      val B = builder.add(new FramingStage(frameLen))
      val C = builder.add(new ChecksumCheckStage(frameLen))
      val D = builder.add(new XorStage())

      A ~> B ~> C ~> D
      FlowShape(A.in, D.out)
    }.named(EmitEptByteFrameFlow.getClass.getSimpleName)
  }
}
