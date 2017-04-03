package emitreader

import emitreader.ui.{MainScene, ViewModel}

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage

object EmitReaderApp extends JFXApp {
  stage = new PrimaryStage() {
    val viewModel = new ViewModel()

    title = "Emit Reader"
    scene = new MainScene(viewModel)

    onCloseRequest = handle {
      viewModel.appQuitRequested.setValue(true)
    }
  }
}
