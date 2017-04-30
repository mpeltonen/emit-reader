package emitreader.ui

import emitreader.domain._
import emitreader.sources.binaryfile.BinaryFileSourceType
import emitreader.sources.serialport.SerialPortSourceType
import emitreader.targets.binaryfile.BinaryFileTargetType
import emitreader.targets.rogainmanager.RogainManagerTargetType
import emitreader.targets.uitable.UiTableTargetType

import scalafx.beans.property.{BooleanProperty, ObjectProperty, Property}
import scalafx.collections.ObservableBuffer

class ViewModel {
  val sourceTypes: ObservableBuffer[EmitDataSourceType] = ObservableBuffer(new SerialPortSourceType, new BinaryFileSourceType())
  val selectedSourceType: Property[EmitDataSourceType, EmitDataSourceType] = ObjectProperty(sourceTypes(0))

  val decoderTypes: ObservableBuffer[DecoderType] = ObservableBuffer(FullDecoderType(), SplitTimeOnlyDecoderType())
  val selectedDecoderType: Property[DecoderType, DecoderType] = ObjectProperty(decoderTypes(0))

  val targetTypes: ObservableBuffer[EmitDataTargetType[_]] = ObservableBuffer(new RogainManagerTargetType(), new UiTableTargetType())
  val selectedTargetType: Property[EmitDataTargetType[_], EmitDataTargetType[_]] = ObjectProperty(targetTypes(0))

  val isStarted: Property[Boolean, java.lang.Boolean] = BooleanProperty(false)
  val appQuitRequested: Property[Boolean, java.lang.Boolean] = BooleanProperty(false)
  val isSourceReadyToStart: Property[Boolean, java.lang.Boolean] = BooleanProperty(false)
  val isTargetReadyToStart: Property[Boolean, java.lang.Boolean] = BooleanProperty(false)
}
