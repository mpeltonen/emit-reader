package emitreader.sources.emulator

import scalafx.beans.property.ObjectProperty

class MutablePunch(controlCode_ : Int, splitTime_ : Int) {
  val controlCode = new ObjectProperty[Int](this, "controlCode", controlCode_)
  val splitTime = new ObjectProperty[Int](this, "splitTime", splitTime_)
}

