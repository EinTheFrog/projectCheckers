package view

import javafx.beans.property.ReadOnlyDoubleProperty
import javafx.geometry.Pos
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import model.Board
import model.Vector
import tornadofx.add
import tornadofx.gridpane
import tornadofx.rectangle

/**
 * Хранит графическое представление клеток поля и логическое представление доски
 */
class BoardView(
        heightProperty: ReadOnlyDoubleProperty,
        widthProperty: ReadOnlyDoubleProperty,
        val board: Board,
        onCellClick: (CellView) -> Unit
): StackPane() {
    private val cells = Array(8) {Array<CellView?>(8) {null} }
    init {
        //запрещаем фокусироваться на доску
        isFocusTraversable = true
        //создаем границы доски
        rectangle {
            heightProperty().bind(heightProperty)
            widthProperty().bind(widthProperty)
            fill = Color.DIMGRAY
        }
        val boardHeight = heightProperty.multiply(0.9)
        val boardWidth = widthProperty.multiply(0.9)

        //создаем клетки
        gridpane {
            this.alignment = Pos.CENTER
            val cellHeight = boardHeight.divide(8)
            val cellWidth = boardWidth.divide(8)
            for (i in 0..7) {
                for (j in 0..7) {
                    val color = if ((i + j) % 2 == 0) Color.DARKGRAY else Color.GRAY
                    val cell = CellView(cellHeight, cellWidth, Vector(i, j), color, board[i, j], onCellClick)
                    cells[i][j] = cell
                    add(cell)
                }
            }
        }
    }

    //функции для удобства взаимодействия с клетками поля
    operator fun get(x: Int, y: Int) = cells[x][y]

    operator fun set(x: Int, y: Int, piece: PieceView?) {
        cells[x][y]!!.piece = piece
    }
}