package view

import javafx.beans.property.ReadOnlyDoubleProperty
import javafx.geometry.Pos
import javafx.scene.input.KeyCode
import javafx.scene.layout.GridPane
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import model.*
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
    private val gridPane: GridPane
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
        gridPane = gridpane {
            this.alignment = Pos.CENTER
            val cellHeight = boardHeight.divide(8)
            val cellWidth = boardWidth.divide(8)
            for (i in 0..7) {
                for (j in 0..7) {
                    val color = if ((i + j) % 2 == 0) Color.DARKGRAY else Color.GRAY
                    val cell = CellView(cellHeight, cellWidth, Vector(i, j), color, onCellClick)
                    cells[i][j] = cell
                    cell.addPieceIfNeeded(i, j)
                    add(cell)
                }
            }
        }
    }

    private fun CellView.addPieceIfNeeded(i: Int, j: Int) {
        val playerColor = if (playerColorInd == 0) Color.BLACK else Color.WHITE
        val enemyColor = if (playerColorInd == 0) Color.WHITE else Color.BLACK
        val enemyColorInd = (playerColorInd + 1) % 2
        if (j < 3 && (i + j) % 2 != 0) {
            board[i, j] = Piece(i * 8 + j, PieceType.CHECKER, Vector(i, j), enemyColorInd, Direction.DOWN)
            this.piece = PieceView(this.heightProperty(), this.widthProperty(), enemyColor)
        } else if (j > 4 && (i + j) % 2 != 0) {
            board[i, j] = Piece(i * 8 + j, PieceType.CHECKER, Vector(i, j), playerColorInd, Direction.UP)
            this.piece = PieceView(this.heightProperty(), this.widthProperty(), playerColor)
        }
    }

    fun refresh() {
        board.refresh()
        for (i in 0..7) {
            for (j in 0..7) {
                removePiece(Vector(i, j))
                val cell = cells[i][j]!!
                cell.addPieceIfNeeded(i, j)
            }
        }
    }

    fun changePiecePos(oldPos: Vector, newPos: Vector) {
        val piece = cells[oldPos.x][oldPos.y]!!.piece!!
        cells[oldPos.x][oldPos.y]!!.piece = null
        cells[newPos.x][newPos.y]!!.piece = piece
        if (board[newPos.x, newPos.y].piece!!.type == PieceType.KING) {
            piece.becomeKing()
        }
    }

    fun removePiece(pos: Vector) {
        cells[pos.x][pos.y]!!.piece = null
    }

    //функции для удобства взаимодействия с клетками поля
    operator fun get(x: Int, y: Int) = cells[x][y]

    operator fun set(x: Int, y: Int, piece: PieceView?) {
        cells[x][y]!!.piece = piece
    }
}