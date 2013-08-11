
package object emitreader {
  type ReadingTime = Long
  type EmitCardId = Long
  type SplitTime = Long
  type ControlCode = Int
  type LowBattery = Boolean
  type Punches = Seq[(ControlCode, SplitTime, LowBattery)]
}
