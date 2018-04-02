package emitreader.ui

import scalafx.scene.control.Label

class TitleLabel(title: String) extends Label(title) {
  styleClass.setAll("title-label")
}
