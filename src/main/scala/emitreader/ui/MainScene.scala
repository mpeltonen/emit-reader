package emitreader.ui

import java.awt.Toolkit
import javafx.geometry.HPos
import javafx.scene.layout.Priority

import scalafx.geometry.Orientation
import scalafx.scene.Scene
import scalafx.scene.control._
import scalafx.scene.layout.{ColumnConstraints, GridPane, RowConstraints}

class MainScene(viewModel: ViewModel) extends Scene() {
  val controller = new Controller(viewModel)
  val sourceSelectionPane = new SourceSelectionPane(viewModel)
  val targetSelectionPane = new TargetSelectionPane(viewModel)
  val startStopPane = new StartStopPane(controller, viewModel)

  stylesheets.add(getClass.getResource("/css/application.css").toExternalForm)

  root = new GridPane() {
    columnConstraints = Seq(
      new ColumnConstraints(300, 300, screenWidth / 4, Priority.ALWAYS, HPos.LEFT, true),
      new ColumnConstraints(5), // Separator
      new ColumnConstraints(200, 200, 200, Priority.ALWAYS, HPos.LEFT, true),
      new ColumnConstraints(5), // Separator
      new ColumnConstraints(280, 700, Double.MaxValue, Priority.ALWAYS, HPos.LEFT, true)
    )

    rowConstraints = Seq(new RowConstraints(768, 768, screenHeight) {
      percentHeight = 100
    })

    addRow(0, sourceSelectionPane, separator(), startStopPane, separator(), targetSelectionPane)
  }

  def separator() = new Separator() { orientation = Orientation.Vertical }

  val screenWidth: Int = Toolkit.getDefaultToolkit.getScreenSize.width
  val screenHeight: Int = Toolkit.getDefaultToolkit.getScreenSize.height
}
