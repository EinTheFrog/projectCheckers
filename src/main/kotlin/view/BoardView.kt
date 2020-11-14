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
import java.lang.IllegalArgumentException

/**
 * Хранит графическое представление клеток поля и логическое представление доски
 */
class BoardView(
        heightProperty: ReadOnlyDoubleProperty,
        widthProperty: ReadOnlyDoubleProperty,
        private val board: Board,
        onCellClick: (CellView) -> Unit
): StackPane() {
    private lateinit var cells: Array<Array<CellView>>
    private val gridPane: GridPane
    private var chosenPiece: PieceView? = null
    private val highlightedCells = mutableListOf<CellView>()

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
            cells = Array(8) {i -> Array(8) {j ->
                val color = if ((i + j) % 2 == 0) Color.DARKGRAY else Color.GRAY
                val cell = CellView(cellHeight, cellWidth, Vector(i, j), color, onCellClick)
                cell.addPieceIfNeeded(i, j)
                add(cell)
                cell
            } }
        }
    }

    private fun CellView.addPieceIfNeeded(i: Int, j: Int) {
        val playerColor = if (playerColorInd == 0) Color.BLACK else Color.WHITE
        val enemyColor = if (playerColorInd == 0) Color.WHITE else Color.BLACK
        val enemyColorInd = (playerColorInd + 1) % 2
        if (j < 3 && (i + j) % 2 != 0) {
            val enemyPiece = Piece(i * 8 + j, PieceType.CHECKER, Vector(i, j), enemyColorInd, Direction.DOWN)
            board[i, j] = enemyPiece
            this.piece = PieceView(this.heightProperty(), this.widthProperty(), enemyColor, enemyPiece)
        } else if (j > 4 && (i + j) % 2 != 0) {
            val playerPiece = Piece(i * 8 + j, PieceType.CHECKER, Vector(i, j), playerColorInd, Direction.UP)
            board[i, j] = playerPiece
            this.piece = PieceView(this.heightProperty(), this.widthProperty(), playerColor, playerPiece)
        }
    }

    fun refresh() {
        board.refresh()
        for (i in 0..7) {
            for (j in 0..7) {
                removePiece(Vector(i, j))
                val cell = cells[i][j]
                cell.addPieceIfNeeded(i, j)
            }
        }
    }

    fun changePiecePos(oldPos: Vector, newPos: Vector) {
        val piece = cells[oldPos.x][oldPos.y].piece!!
        cells[oldPos.x][oldPos.y].piece = null
        cells[newPos.x][newPos.y].piece = piece
        if (board[newPos.x, newPos.y].piece!!.type == PieceType.KING) {
            piece.becomeKing()
        }
    }

    fun removePiece(pos: Vector) {
        cells[pos.x][pos.y].piece = null
    }

    private fun getCellsForHighlight(piece: Piece): List<CellView> {
        val result = mutableListOf<CellView>()
        val availablePieceMoves = board.getAvailableMovesForPiece(piece)
        val allAvailableMoves = board.getAvailableTurns()

        val playerCanAttack = (allAvailableMoves.values.any{
            moves -> moves.any{ moveList -> moveList.any{ it.isAttack} }
        })
        val currentPieceCanAttack = availablePieceMoves.any{moveList -> moveList.any{ it.isAttack}}
        if (playerCanAttack && !currentPieceCanAttack) return listOf()

        for (moveList in availablePieceMoves) {
            val move = moveList.first()
            val posAfterMove = getMoveCoords(move, piece.pos)
            result.add(this[posAfterMove.x, posAfterMove.y])
        }
        return result
    }

    private fun highLightForPlayer(bool: Boolean) {
        val usingPiece: PieceView = chosenPiece ?: return
        usingPiece.glow(bool)
        for (cell in highlightedCells) {
            cell.highlight(bool)
        }
    }

    fun choosePieceView(pos: Vector?) {
        highLightForPlayer(false)
        if (pos == null) return
        chosenPiece = this[pos.x, pos.y].piece

        val usingPiece: PieceView = chosenPiece ?: return
        highlightedCells.clear()
        highlightedCells.addAll(getCellsForHighlight(usingPiece.piece))
        highLightForPlayer(true)
    }

    private fun getMoveCoords(move: Move, curPos: Vector) = curPos + move.vector

    //функции для удобства взаимодействия с клетками поля
    operator fun get(x: Int, y: Int) = cells[x][y]

    operator fun set(x: Int, y: Int, piece: PieceView?) {
        cells[x][y].piece = piece
    }
}