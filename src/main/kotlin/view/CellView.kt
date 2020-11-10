package view

import javafx.beans.binding.DoubleBinding
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import model.*
import tornadofx.*

/**
 * Хранит графическое предстваление фигуры и логическое представление клетки
 */
class CellView(
        heightProperty: DoubleBinding,
        widthProperty: DoubleBinding,
        val coords: Vector,
        val color: Color,
        onClick: (CellView) -> Unit
): StackPane() {
    //при смене значения piece должны измняться координаты соответствующего pieceView и его логического представления
    var piece: PieceView? = null
    set(value) {
        if (value is PieceView) {
            add(value)
        } else {
            children.remove(piece)
        }
        field = value
    }

    var rectangle: Rectangle
    init {
        //запрещаем фокусироваться на клетках
        isFocusTraversable = true
        rectangle = rectangle {
            this.heightProperty().bind(heightProperty)
            this.widthProperty().bind(widthProperty)
            fill = color
        }
        //устанавливаем положение клетки на доске
        gridpaneConstraints {
            columnRowIndex(coords.x, coords.y)
        }
        //задаем обработку нажатия на клетку при помощи передаваемой функции
        setOnMouseClicked {
            onClick(this)
        }
    }

    fun highlight(bool: Boolean) {
        val highlightColor = if (bool) {
            if (color == Color.BLACK) Color.web("225D52") else Color.web("99EEDE")
        } else color
        rectangle.style {
            fill = highlightColor
        }
    }
}