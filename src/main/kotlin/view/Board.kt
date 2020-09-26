package view

import controller.MyController
import javafx.beans.binding.DoubleBinding
import javafx.beans.property.ReadOnlyDoubleProperty
import javafx.geometry.Pos
import javafx.scene.layout.GridPane
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import model.Board
import model.Piece
import model.PieceType
import model.Vector
import tornadofx.Controller
import tornadofx.add
import tornadofx.gridpane
import tornadofx.rectangle

class BoardView(
    heightProperty: ReadOnlyDoubleProperty,
    widthProperty: ReadOnlyDoubleProperty,
    controller: MyController,
    val board: Board
): StackPane() {
    private val cells = mutableMapOf<Pair<Int, Int>, Cell>()
    init {
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
                    val color = if ((j + i) % 2 == 0) Color.DARKGRAY else Color.GRAY
                    val cell = Cell(cellHeight, cellWidth, Vector(i, j), color, controller)
                    cells[Pair(j, i)] = cell
                    add(cell)
                }
            }
        }
    }
    fun addCheckers () {
        for (i in 0..7) {
            for (j in 0..1) {
                val cell = cells[Pair(j, i)]
                val checker =  Checker(
                        cell!!.heightProperty(),
                        cell!!.widthProperty(),
                        Color.BLACK,
                        Piece(PieceType.CHECKER, Vector(j, i), 0)
                )
                cell?.add(checker)
                board.boardArray[j][i] = checker.piece
            }

            for (j in 6..7) {
                val cell = cells[Pair(j, i)]
                val checker =  Checker(
                        cell!!.heightProperty(),
                        cell!!.widthProperty(),
                        Color.WHITE,
                        Piece(PieceType.CHECKER, Vector(j, i), 1)
                )
                cell?.add(checker)
                board.boardArray[j][i] = checker.piece
            }
        }
    }
}