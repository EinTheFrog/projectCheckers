package controller

import model.*
import tornadofx.Controller
import view.BoardView
import view.CellView
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

    fun clickOnCell(cell: CellView) {
        if (boardView == null) {
            throw IllegalStateException("Board hasn't been set")
        }

        if (!isPlayerTurn) return
        val piece = cell.piece
        if (piece != null && piece.piece.color == 0) {
            choosePiece(piece)
        } else if (chosenPiece != null) {
            val move = defineCorrectMove(chosenPiece!!.piece.pos, cell.coords)
            if (move != null && board?.canPieceMakeThisMove(chosenPiece!!.piece, move) == true) {
                if (move.isAttack) {
                    attackWithPiece(cell)
                } else {
                    movePiece(cell)
                    endTurn()
                }
            }
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
        board!!.turnsMade++
        chosenPiece?.glow(false)
        chosenPiece = null
        isPlayerTurn = false
    }

    private fun movePiece(newCell: CellView) {
        if (chosenPiece == null) {
            throw IllegalStateException("Piece hasn't been set")
        }
        boardView!![chosenPiece!!.piece.pos.x, chosenPiece!!.piece.pos.y] = null
        newCell.piece = chosenPiece
    }

    private fun attackWithPiece(newCell: CellView) {
        if (chosenPiece == null) {
            throw IllegalStateException("Piece hasn't been set")
        }
        val oldCoords = boardView!![chosenPiece!!.piece.pos.x, chosenPiece!!.piece.pos.y]!!.coords
        val attackedCell = boardView!![(oldCoords.x + newCell.coords.x) / 2, (oldCoords.y + newCell.coords.y) / 2]
        attackedCell!!.piece = null
        boardView!![oldCoords.x, oldCoords.y] = null
        newCell.piece = chosenPiece
        if (!board!!.getAvailableMovesForPiece(chosenPiece!!.piece).any{it.first().isAttack}) {
            endTurn()
        }
    }

    private fun defineCorrectMove(curPos: Vector, newPos: Vector): Move? {
        return when(newPos - curPos) {
            Vector(1, 1) -> Move.GO_DOWN_RIGHT
            Vector(-1, 1) -> Move.GO_DOWN_LEFT
            Vector(1, -1) -> Move.GO_UP_RIGHT
            Vector(-1, -1) -> Move.GO_UP_LEFT
            Vector(2, 2) -> Move.ATTACK_DOWN_RIGHT
            Vector(-2, 2) -> Move.ATTACK_DOWN_LEFT
            Vector(2, -2) -> Move.ATTACK_UP_RIGHT
            Vector(-2, -2) -> Move.ATTACK_UP_LEFT
            else -> null
        }
    }

    fun playAITurn() {
        if (board == null) {
            throw IllegalStateException("Board hasn't been set")
        }
        val aiTurn = ai.makeTurn(board!!, 1)
        isPlayerTurn = true
    }
}