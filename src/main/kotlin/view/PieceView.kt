package view

import javafx.beans.property.ReadOnlyDoubleProperty
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Ellipse
import model.Piece
import model.PieceType
import tornadofx.*

/**
 * Хранит логическое предстваление фигуры
 */
class PieceView(
        cellHeight: ReadOnlyDoubleProperty,
        cellWidth: ReadOnlyDoubleProperty,
        color: Color,
): Pane() {
    private var ellipse = Ellipse()
    private var myColor = Color.BLACK
    init {
        isFocusTraversable = true
        ellipse = ellipse {
            radiusYProperty().bind(cellHeight / 3)
            radiusXProperty().bind(cellWidth / 3)
            centerYProperty().bind(cellHeight / 2)
            centerXProperty().bind(cellWidth / 2)
            myColor = color
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

    fun becomeKing() {
        this += rectangle {
            heightProperty().bind(ellipse.radiusYProperty())
            widthProperty().bind(ellipse.radiusXProperty())
            yProperty().bind(ellipse.centerYProperty() - heightProperty() / 2)
            xProperty().bind(ellipse.centerXProperty() - widthProperty() / 2)
            fill = myColor.invert()
        }
    }
}