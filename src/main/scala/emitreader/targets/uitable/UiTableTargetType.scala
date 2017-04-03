package emitreader.targets.uitable

import emitreader.domain.{EmitDataTarget, EmitDataTargetType}
import emitreader.ui.ViewModel

import scalafx.scene.layout.Pane

class UiTableTargetType extends EmitDataTargetType {
  val localViewModel = new UiTableTargetViewModel()

  override val displayName = "UI Table"

  override def getUiPane(viewModel: ViewModel): Pane = new UiTableTargetViewPane(viewModel, localViewModel)

  override def startTarget(): EmitDataTarget = new UiTableTarget(localViewModel)
}
