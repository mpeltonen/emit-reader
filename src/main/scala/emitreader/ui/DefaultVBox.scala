package emitreader.ui

import scalafx.geometry.Insets
import scalafx.scene.Node
import scalafx.scene.layout.VBox

class DefaultVBox(_children: Seq[Node]) extends VBox {
  padding = Insets(5)
  spacing = 5
  children = _children

  def this() = this(Seq.empty)
}
