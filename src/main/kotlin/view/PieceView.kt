package view

import javafx.beans.property.ReadOnlyDoubleProperty
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Ellipse
import model.Piece
import tornadofx.*

/**
 * Хранит логическое предстваление фигуры
 */
class PieceView(
        cellHeight: ReadOnlyDoubleProperty,
        cellWidth: ReadOnlyDoubleProperty,
        color: Color,
        val piece: Piece
): Pane() {
    private var ellipse = Ellipse()
    init {
        isFocusTraversable = true
        ellipse = ellipse {
            radiusYProperty().bind(cellHeight / 3)
            radiusXProperty().bind(cellWidth / 3)
            centerYProperty().bind(cellHeight / 2)
            centerXProperty().bind(cellWidth / 2)
            fill = color
        }
        this += ellipse
    }

    fun glow(bool: Boolean) {
        val color = if (bool) Color.YELLOW else Color.BLACK
        ellipse.style {
            stroke = color
        }
    }
}