package emitreader.targets.uitable

import emitreader.domain.EmitCard
import emitreader.ui.{DefaultVBox, ViewModel}

import scalafx.beans.property.ObjectProperty
import scalafx.scene.control.{TableColumn, TableView}
import scalafx.scene.layout.Priority

class UiTableTargetViewPane(globalViewModel: ViewModel, localViewModel: UiTableTargetViewModel) extends DefaultVBox {
  val col1 = new TableColumn[EmitCard, String]("Reading Time") {
    cellValueFactory = { emitCard => ObjectProperty(emitCard.value, "formattedReadingTime", emitCard.value.formattedReadingTime) }
    prefWidth = 180
  }

  val col2 = new TableColumn[EmitCard, Long]("Card Id") {
    cellValueFactory = { emitCard => ObjectProperty(emitCard.value, "cardId", emitCard.value.cardId) }
    prefWidth = 80
  }

  val col3 = new TableColumn[EmitCard, String]("Punches") {
    cellValueFactory = { emitCard => ObjectProperty(emitCard.value, "punches", emitCard.value.punches.toString()) }
    prefWidth = 1240
  }

  val tv = new TableView[EmitCard](localViewModel.tableViewModel) {
    columns ++= List(col1, col2, col3)
    maxWidth = Double.MaxValue
  }

  scalafx.scene.layout.VBox.setVgrow(tv, Priority.Always)

  children = Seq(tv)
}
