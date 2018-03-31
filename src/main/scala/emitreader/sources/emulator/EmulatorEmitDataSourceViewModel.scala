package emitreader.sources.emulator

import io.reactivex.subjects.PublishSubject

import scalafx.beans.property.{Property, StringProperty}
import scalafx.collections.ObservableBuffer

class EmulatorEmitDataSourceViewModel {
  val cardId: Property[String, String] = new StringProperty()
  val punches = ObservableBuffer[MutablePunch]()
  val sendDataButtonClicks: PublishSubject[Unit] = PublishSubject.create()
}
