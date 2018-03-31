package emitreader.ui

import scalafx.geometry.Insets
import scalafx.scene.Node
import scalafx.scene.layout.HBox

class DefaultHBox(_children: Seq[Node]) extends HBox {
  padding = Insets(5)
  spacing = 5
  children = _children

  def this() = this(Seq.empty)
}
