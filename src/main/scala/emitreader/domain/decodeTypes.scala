package emitreader.domain

sealed abstract class DecoderType(val displayName: String, val frameLen: Int) {
  override def toString: String = displayName
}

case class FullDecoderType() extends DecoderType("Full (250)", 217)
case class SplitTimeOnlyDecoderType() extends DecoderType("Online", 10)
