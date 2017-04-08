package emitreader.flow

import akka.NotUsed
import akka.stream.{FlowShape, Graph}
import akka.stream.scaladsl.GraphDSL
import akka.stream.scaladsl.GraphDSL.Implicits._
import akka.util.ByteString
import emitreader.domain.EmitData
import emitreader.flow.stages.{ChecksumCheckStage, DecodeDataStage, FramingStage, XorStage}

object EmitEptDecodeFlow {
  def apply(frameLen: Int): Graph[FlowShape[ByteString, EmitData], NotUsed] = {
    GraphDSL.create() { implicit builder =>
      val A = builder.add(new XorStage())
      val B = builder.add(new FramingStage(frameLen))
      val C = builder.add(new ChecksumCheckStage(frameLen))
      val D = builder.add(new DecodeDataStage(frameLen))

      A ~> B ~> C ~> D
      FlowShape(A.in, D.out)
    }.named(EmitEptDecodeFlow.getClass.getSimpleName)
  }
}
