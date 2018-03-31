package emitreader.sources.emulator

import akka.actor.ActorRef
import akka.util.ByteString
import emitreader.domain.EmitDataSource

import scalafx.collections.ObservableBuffer

class EmulatorEmitDataSource(flowSourceActor: ActorRef, viewModel: EmulatorEmitDataSourceViewModel) extends EmitDataSource {
  viewModel.sendDataButtonClicks.subscribe(_ => {
    flowSourceActor ! emitEncode(viewModel.cardId.value, viewModel.punches)
  })

  override def terminate(): Unit = {}

  def emitEncode(cardId: String, punches: ObservableBuffer[MutablePunch]): ByteString = {
    val ba: Array[Byte] = Array.fill(217)(0)

    ba(0) = 0xFF.toByte
    ba(1) = 0xFF.toByte

    ba(2) = (cardId.toLong & 0xFF).toByte
    ba(3) = ((cardId.toLong & 0xFF00) >> 8).toByte
    ba(4) = ((cardId.toLong & 0xFF0000) >> 16).toByte

    ba(9) = (256 - (ba.view(2, 8).sum % 256)).toByte

    punches.toList.zipWithIndex.foreach { case (punch, index) => {
      ba(10 + (index * 3)) = (punch.controlCode.value & 0xFF).toByte
      ba(11 + (index * 3)) = (punch.splitTime.value & 0xFF).toByte
      ba(12 + (index * 3)) = ((punch.splitTime.value & 0xFF00) >> 8).toByte
    }}

    ba(216) = (256 - (ba.sum % 256)).toByte

    ByteString.fromArray(ba.map(b => (b ^ 0xDF).toByte))
  }
}
