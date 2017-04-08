package emitreader.targets.uitable

import emitreader.domain.EmitData

import scalafx.collections.ObservableBuffer

class UiTableTargetViewModel {
  val tableViewModel: ObservableBuffer[EmitData] = ObservableBuffer()
}
