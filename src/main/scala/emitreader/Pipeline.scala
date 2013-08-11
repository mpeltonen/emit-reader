package emitreader

import akka.util.{ByteIterator, ByteString}
import java.nio.ByteOrder
import scala.annotation.tailrec
import akka.io.{PipePair, PipelineStage, PipelineContext}
import scala.collection.mutable.ListBuffer
import scala.Int

object Pipeline {
  val stages = new DecodeDataStage >> new ChecksumCheckStage >> new FramingStage >> new XorStage

  object AllControls extends EmitEptPipelineContext {
    val frameLen = 217
  }

  object SplitTimeOnly extends EmitEptPipelineContext {
    val frameLen = 10
    override def checksumBytes(frame: ByteString) = frame.drop(2)
  }

  abstract class EmitEptPipelineContext extends PipelineContext {
    val frameLen: Int
    def includeControls: Boolean = frameLen > 10
    protected def checksumBytes(frame: ByteString): ByteString = frame

    def checksumOk(frame: ByteString): Boolean = frame.nonEmpty && checksumBytes(frame).sum % 256 == 0
  }

  private class DecodeDataStage extends EventsOnlyPipelineStage[EmitEptPipelineContext, (ReadingTime, EmitCardId, Punches), ByteString] {
    implicit val byteOrder = ByteOrder.LITTLE_ENDIAN
    def isLowBattery(code: ControlCode) = code == 99
    @inline def unsigned(b: Byte) = b & 0xFF

    private def decodeControlData(bytes: ByteIterator): Seq[(ControlCode, SplitTime, LowBattery)] = {
      bytes.grouped(3).take(50).foldLeft((Seq[(ControlCode, SplitTime, LowBattery)](), false)) { (acc, ctrlData) =>
        val (controlCode, punchTime) = (unsigned(ctrlData(0)), (unsigned(ctrlData(1)) | unsigned(ctrlData(2)) << 8).toLong)
        if (isLowBattery(controlCode)) (acc._1, true)
        else (acc._1 :+ (controlCode, punchTime, false), false)
      }._1
    }

    override def apply(ctx: EmitEptPipelineContext) = new EventsOnlyPipePair[(ReadingTime, EmitCardId, Punches), ByteString] {
      def eventPipeline = { frame: ByteString =>
        val iterator = frame.iterator
        // See http://ttime.no/rs232.pdf (last page) for protocol description
        iterator.drop(2)
        val cardId = iterator.getLongPart(3)
        iterator.drop(5)
        val controlData = if (ctx.includeControls) decodeControlData(iterator) else Seq.empty

        ctx.singleEvent((System.currentTimeMillis(), cardId, controlData))
      }
    }
  }

  private class ChecksumCheckStage extends EventsOnlyPipelineStage[EmitEptPipelineContext, ByteString, ByteString] {
    override def apply(ctx: EmitEptPipelineContext) = new EventsOnlyPipePair[ByteString, ByteString] {
      def eventPipeline = { frame: ByteString =>
        if (ctx.checksumOk(frame)) ctx.singleEvent(frame)
        else Seq.empty
      }
    }
  }

  private class FramingStage extends EventsOnlyPipelineStage[EmitEptPipelineContext, ByteString, ByteString] {
    override def apply(ctx: EmitEptPipelineContext) = new EventsOnlyPipePair[ByteString, ByteString] {
      var buffer = None: Option[ByteString]

      def framePrefixes(data: ByteString): Seq[Int] = {
        for (
          i <- 1 until data.length
          if (data(i - 1) == 0xFF.toByte && data(i) == 0xFF.toByte)
        ) yield i - 1
      }

      @tailrec
      def extractFrames(data: ByteString, acc: List[ByteString]): (Option[ByteString], Seq[ByteString]) = {
        if (data.isEmpty) {
          (None, acc)
        } else if (data.length < ctx.frameLen) {
          (Some(data), acc)
        } else {
          val frameStarts = framePrefixes(data)
          val (startIndex, endIndex) = frameStarts match {
            case Nil                      => (0, 0)
            case Seq(fst)                 => (fst, data.length)
            case Seq(fst, snd, rest @ _*) => (fst, snd)
          }
          val bytesBetweenFrameStarts = endIndex - startIndex
          val isIncompleteFrame = bytesBetweenFrameStarts < ctx.frameLen && endIndex == data.length
          val dropIncompleteFrame = bytesBetweenFrameStarts < ctx.frameLen && endIndex < data.length
          if (frameStarts.isEmpty) {
            (None, acc)
          } else if (isIncompleteFrame) {
            (Some(data drop startIndex), Nil)
          } else if (dropIncompleteFrame) {
            (Some(data drop endIndex), Nil)
          } else {
            extractFrames(data drop endIndex, data.slice(startIndex, startIndex + ctx.frameLen) :: acc)
          }
        }
      }

      def eventPipeline = { bs: ByteString =>
        val data = if (buffer.isEmpty) bs else buffer.get ++ bs
        val (remaining, frames) = extractFrames(data, Nil)
        buffer = remaining
        frames match {
          case Nil        => Nil
          case one :: Nil => ctx.singleEvent(one)
          case many       => many reverseMap (Left(_))
        }
      }
    }
  }

  private class XorStage extends EventsOnlyPipelineStage[EmitEptPipelineContext, ByteString, ByteString] {
    override def apply(ctx: EmitEptPipelineContext) = new EventsOnlyPipePair[ByteString, ByteString] {
      def eventPipeline = bs => ctx.singleEvent(bs.map(b => (b ^ 0xDF.toByte).toByte))
    }
  }

  private abstract class EventsOnlyPipelineStage[C <: PipelineContext, EvtAbove, EvtBelow] extends PipelineStage[C, Unit, Unit, EvtAbove, EvtBelow] {
    trait EventsOnlyPipePair[EvtAbove, EvtBelow] extends PipePair[Unit, Unit, EvtAbove, EvtBelow] {
      def commandPipeline = { _ => Seq(Right(Unit))}
    }
  }
}
