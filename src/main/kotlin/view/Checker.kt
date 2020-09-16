package view

import javafx.beans.property.ReadOnlyDoubleProperty
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import tornadofx.*

class Checker(
        cellHeight: ReadOnlyDoubleProperty,
        cellWidth: ReadOnlyDoubleProperty,
        color: Color
): Pane() {
    init {
        isFocusTraversable = true
        this += ellipse {
            radiusYProperty().bind(cellHeight / 3)
            radiusXProperty().bind(cellWidth / 3)
            centerYProperty().bind(cellHeight / 2)
            centerXProperty().bind(cellWidth / 2)
            fill = color
        }
    }
}