package view

import controller.MyController
import javafx.beans.binding.DoubleBinding
import javafx.scene.Node
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import model.Cell
import model.Piece
import model.PieceType
import model.Vector
import tornadofx.*

class CellView(
        heightProperty: DoubleBinding,
        widthProperty: DoubleBinding,
        val coords: Vector,
        color: Color,
        controller: MyController,
        private val cell: Cell
): StackPane() {
    var piece: PieceView? = null
    set(value) {
        field = value
        cell.piece = value?.piece
        if (value is PieceView) {
            add(value)
        }
    }

    init {
        isFocusTraversable = true
        rectangle {
            this.heightProperty().bind(heightProperty)
            this.widthProperty().bind(widthProperty)
            fill = color
        }
        gridpaneConstraints {
            columnRowIndex(coords.x, coords.y)
        }
        setOnMouseClicked {
            controller.clickOnCell(this)
        }
        if (coords.y < 3 && (coords.x + coords.y) % 2 != 0) {
            piece = PieceView(heightProperty(), widthProperty(), Color.BLACK, Piece(PieceType.CHECKER, coords, 0))
        } else if (coords.y > 4 && (coords.x + coords.y) % 2 != 0) {
            piece = PieceView(heightProperty(), widthProperty(), Color.WHITE, Piece(PieceType.CHECKER, coords, 1))
        }
    }
}