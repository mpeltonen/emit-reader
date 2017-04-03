package emitreader.ui

import scalafx.geometry.Orientation
import scalafx.scene.Scene
import scalafx.scene.control._

class MainScene(viewModel: ViewModel) extends Scene(1024, 768) {
  val controller = new Controller(viewModel)
  val sourcePane = new SourcePane(viewModel)
  val targetPane = new TargetPane(viewModel)
  val startStopPane = new StartStopPane(controller, viewModel)

  root = new DefaultHBox {
    children = Seq(sourcePane, separator(), startStopPane, separator(), targetPane)
  }

  def separator() = new Separator() { orientation = Orientation.Vertical }
}
