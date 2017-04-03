package emitreader.domain

import emitreader.ui.ViewModel

import scalafx.scene.layout.Pane

trait EmitDataTargetType {
  val displayName: String

  def getUiPane(viewModel: ViewModel): Pane
  def startTarget(): EmitDataTarget

  override def toString() = displayName
}
