package emitreader.sources.emulator

import emitreader.ui.{DefaultHBox, DefaultVBox, ViewModel}

import scalafx.beans.binding.Bindings.createBooleanBinding
import scalafx.beans.property.StringProperty
import scalafx.scene.control.TableColumn.sfxTableColumn2jfx
import scalafx.scene.control._
import scalafx.scene.control.cell.TextFieldTableCell
import scalafx.util.converter.IntStringConverter

class EmulatorEmitDataSourceViewPane(globalViewModel: ViewModel, localViewModel: EmulatorEmitDataSourceViewModel) extends DefaultVBox {
  val punches = localViewModel.punches

  val cardId = new DefaultVBox {
    val cardIdLabel = new Label("Card ID:")
    val cardIdField = new TextField {
      text <==> localViewModel.cardId
    }
    children = Seq(cardIdLabel, cardIdField)
  }

  val addRemoveButtons = new DefaultHBox {
    children = Seq(
      new Button("+") {
        onAction = _ => {
          val maxControlCode = if (punches.length > 0) punches(punches.length - 1).controlCode.value + 1 else 0
          val maxSplitTime = if (punches.length > 0) punches(punches.length - 1).splitTime.value + 1 else 0
          punches.add(new MutablePunch(maxControlCode, maxSplitTime))
        }
      },
      new Button("-") {
        onAction = _ => if (punches.length > 1) punches.remove(punches.length - 1)
      }
    )
  }

  val sendDataButton = new Button("Send data") {
    onAction = _ => localViewModel.sendDataButtonClicks.onNext()
  }

  sendDataButton.disable <== createBooleanBinding(() => !globalViewModel.isStarted.getValue, globalViewModel.isStarted)

  val tableView = new TableView[MutablePunch](localViewModel.punches) {
    editable = true
    selectionModel().cellSelectionEnabledProperty.set(true)

    columns ++= List(
      new TableColumn[MutablePunch, String] {
        text = "Control"
        editable = false
        cellValueFactory = p => new StringProperty(String.valueOf(p.tableView.getItems.indexOf(p.value) + 1))
      },
      new TableColumn[MutablePunch, Int] {
        text = "Code"
        cellValueFactory = _.value.controlCode
        cellFactory = TextFieldTableCell.forTableColumn[MutablePunch, Int](new IntStringConverter())
        prefWidth = 100
      },
      new TableColumn[MutablePunch, Int] {
        text = "Split time"
        cellValueFactory = _.value.splitTime
        cellFactory = TextFieldTableCell.forTableColumn[MutablePunch, Int](new IntStringConverter())
        prefWidth = 100
      }
    )
    prefHeight = 600
  }

  children = Seq(cardId, tableView, addRemoveButtons, sendDataButton)
}
