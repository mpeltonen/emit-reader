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
  val sourcePane = new SourcePane(viewModel)
  val targetPane = new TargetPane(viewModel)
  val startStopPane = new StartStopPane(controller, viewModel)

  root = new GridPane() {
    val (col1Constr, col2Constr, col3Constr) = (
      new ColumnConstraints(300, 300, screenWidth / 4, Priority.ALWAYS, HPos.LEFT, true),
      new ColumnConstraints(200, 200, 200, Priority.ALWAYS, HPos.LEFT, true),
      new ColumnConstraints(280, 700, Double.MaxValue, Priority.ALWAYS, HPos.LEFT, true),
    )
    val separatorColConstr = new ColumnConstraints(5)
    columnConstraints = Seq(col1Constr, separatorColConstr, col2Constr, separatorColConstr, col3Constr)

    val rowConstr = new RowConstraints(768, 768, screenHeight)
    rowConstr.percentHeight = 100
    rowConstraints = Seq(rowConstr)

    addRow(0, sourcePane, separator(), startStopPane, separator(), targetPane)
  }

  def separator() = new Separator() { orientation = Orientation.Vertical }

  val screenWidth: Int = Toolkit.getDefaultToolkit.getScreenSize.width
  val screenHeight: Int = Toolkit.getDefaultToolkit.getScreenSize.height
}
