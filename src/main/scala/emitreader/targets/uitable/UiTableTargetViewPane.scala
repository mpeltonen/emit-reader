package emitreader.targets.uitable

import emitreader.domain.EmitData
import emitreader.ui.{DefaultVBox, ViewModel}

import scalafx.beans.property.ObjectProperty
import scalafx.scene.control.{TableColumn, TableView}
import scalafx.scene.layout.Priority

class UiTableTargetViewPane(globalViewModel: ViewModel, localViewModel: UiTableTargetViewModel) extends DefaultVBox {
  val col1 = new TableColumn[EmitData, String]("Reading Time") {
    cellValueFactory = { emitCard => ObjectProperty(emitCard.value, "formattedReadingTime", emitCard.value.formattedReadingTime) }
    prefWidth = 180
  }

  val col2 = new TableColumn[EmitData, Long]("Card Id") {
    cellValueFactory = { emitCard => ObjectProperty(emitCard.value, "cardId", emitCard.value.cardId) }
    prefWidth = 80
  }

  val col3 = new TableColumn[EmitData, String]("Punches") {
    cellValueFactory = { emitCard => ObjectProperty(emitCard.value, "punches", emitCard.value.punches.toString()) }
    prefWidth = 1240
  }

  val tv = new TableView[EmitData](localViewModel.tableViewModel) {
    columns ++= List(col1, col2, col3)
    maxWidth = Double.MaxValue
  }

  scalafx.scene.layout.VBox.setVgrow(tv, Priority.Always)

  children = Seq(tv)
}
