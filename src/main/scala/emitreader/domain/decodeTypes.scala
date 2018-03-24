package emitreader.domain

sealed abstract class DecoderType(val displayName: String, val frameLen: Int) {
  override def toString: String = displayName
}

case class FullDecoderType() extends DecoderType("Full card", 217)
case class SplitTimeOnlyDecoderType() extends DecoderType("Split time only", 10)
