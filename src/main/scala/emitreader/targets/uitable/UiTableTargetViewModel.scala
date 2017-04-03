package emitreader.targets.uitable

import emitreader.domain.EmitCard

import scalafx.collections.ObservableBuffer

class UiTableTargetViewModel {
  val tableViewModel: ObservableBuffer[EmitCard] = ObservableBuffer()
}
