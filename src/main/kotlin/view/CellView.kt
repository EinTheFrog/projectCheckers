package view

import javafx.beans.binding.DoubleBinding
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import model.*
import tornadofx.*

/**
 * Хранит графическое предстваление фигуры и логическое представление клетки
 */
class CellView(
        heightProperty: DoubleBinding,
        widthProperty: DoubleBinding,
        val coords: Vector,
        color: Color,
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

    init {
        //запрещаем фокусироваться на клетках
        isFocusTraversable = true
        rectangle {
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
        //задаем характеристики фигур при расстановка в зависимости от расположения клетки
        if (coords.y < 3 && (coords.x + coords.y) % 2 != 0) {
            piece = PieceView(heightProperty(), widthProperty(), Color.BLACK)
        } else if (coords.y > 4 && (coords.x + coords.y) % 2 != 0) {
            piece = PieceView(heightProperty(), widthProperty(), Color.WHITE)
        }
    }
}