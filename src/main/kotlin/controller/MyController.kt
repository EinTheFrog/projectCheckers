package controller

import javafx.stage.Modality
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import model.*
import tornadofx.Controller
import view.BoardView
import view.CellView
import view.GameMenu
import view.PieceView

class MyController: Controller() {
    private var board: Board? = null

    var boardView: BoardView? = null
    set(value) {
        field = value
        board = boardView?.board
    }

    var chosenPiece: PieceView? = null
    var isPlayerTurn = true
    private val ai = AI()

    //функция для обработки нажатия на клетку
    fun clickOnCell(cell: CellView) {
        //мы должны знать доску, на которой находится клетка
        if (boardView == null) {
            throw IllegalStateException("Board hasn't been set")
        }

        //если сейчас ход ИИ, то игрок не может двигать фигуры
        if (!isPlayerTurn) return

        val piece = cell.piece
        if (piece != null && piece.piece.color == 0) {
            //если клетка не пустая и фигура черная (пока игрок может играть только за черных), то выбираем фигуру
            choosePiece(piece)
        } else if (chosenPiece != null) {//если игрок уже выбрал фигуру, то ходим ей
            //определяем какой ход хочет сделать игрок по взаимному расположению выбранной фигуры и нажатой клетки
            val move = defineCorrectMove(chosenPiece!!.piece.pos, cell.coords)
            if (move != null && board?.canPieceMakeThisMove(chosenPiece!!.piece, move) == true) {
                if (move.isAttack) {
                    attackWithPiece(cell)
                } else if (!board!!.getAvailableTurns().values.any{it.any{it.any{it.isAttack}}}){
                    movePiece(cell)
                    endTurn()
                } else {
                    chosenPiece?.glow(false)
                    chosenPiece = null
                }
            }
        }
    }

    fun onEsc() {
        if (chosenPiece != null) {
            choosePiece(null)
        } else {
            find<GameMenu>().openWindow()
        }
    }
    fun choosePiece(piece: PieceView?) {
        chosenPiece?.glow(false)
        chosenPiece = piece
        chosenPiece?.glow(true)
    }

    private fun endTurn() {
        if (board == null) {
            throw IllegalStateException("Board hasn't been set")
        }
        //увеличиваем кол-во ходов, обнуляем выбранную фигуру
        board!!.turnsMade++
        chosenPiece?.glow(false)
        chosenPiece = null
        isPlayerTurn = board!!.turnsMade % 2 == 0
    }

    private fun movePiece(newCell: CellView) {
        if (chosenPiece == null) {
            throw IllegalStateException("Piece hasn't been set")
        }
        //убираем фигуру со старой клетки и ставим на новую
        boardView!![chosenPiece!!.piece.pos.x, chosenPiece!!.piece.pos.y] = null
        newCell.piece = chosenPiece
    }

    private fun attackWithPiece(newCell: CellView) {
        if (chosenPiece == null) {
            throw IllegalStateException("Piece hasn't been set")
        }
        val oldCoords = boardView!![chosenPiece!!.piece.pos.x, chosenPiece!!.piece.pos.y]!!.coords
        val attackedCell = boardView!![(oldCoords.x + newCell.coords.x) / 2, (oldCoords.y + newCell.coords.y) / 2]
        //убираем с клетки атакованную фигуру
        attackedCell!!.piece = null
        //убираем фигуру со старой клетки и ставим на новую
        boardView!![oldCoords.x, oldCoords.y] = null
        newCell.piece = chosenPiece
        if (!board!!.getAvailableMovesForPiece(chosenPiece!!.piece).any{it.first().isAttack}) {
            endTurn()
        }
    }

    private fun defineCorrectMove(curPos: Vector, newPos: Vector): Move? = Move.values().find { it.vector ==  newPos - curPos}

    fun playAITurn() {
        if (board == null) {
            throw IllegalStateException("Board hasn't been set")
        }
        //определяем ход ИИ
        val aiTurn = ai.makeTurn(board!!, 1)
        //выбираем фигуру по ходу ИИ
        chosenPiece = boardView!![aiTurn.piece.pos.x, aiTurn.piece.pos.y]!!.piece
        //последовательно совершаем все ходы ИИ
        for (move in aiTurn.moves) {
            val newCell = boardView!![aiTurn.piece.pos.x + move.vector.x, aiTurn.piece.pos.y + move.vector.y]!!
            if (move.isAttack) {
                attackWithPiece(newCell)
            }
            else {
                movePiece(newCell)
                endTurn()
            }
            runBlocking { launch { suspend { 100 } } }
        }
    }
}