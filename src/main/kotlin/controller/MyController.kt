package controller

import model.*
import tornadofx.Controller
import view.CellView
import view.PieceView

class MyController(val board: Board): Controller() {
    val player = Player(board)
    val ai = AI()
    var chosenCell: CellView? = null
    var isPlayerTurn = true
    val moves = mutableListOf<Move>()

    fun clickOnCell(cell: CellView) {
        if (!isPlayerTurn) return
        val piece = cell.piece
        if (piece != null) {
            chooseCellWithPiece(cell)
        } else if (chosenCell?.piece != null) {
            val move = defineCorrectMove(chosenCell!!.coords, cell.coords)
            if (move != null) {
                moves.add(move)
                if (move.isAttack) {
                    movePiece(cell)
                } else {
                    makeTurn(Turn(chosenCell!!.piece!!.piece, moves), cell)
                }
            }
        }
    }
    fun chooseCellWithPiece(cell: CellView?) {
        chosenCell?.piece?.glow(false)
        chosenCell = cell
        chosenCell?.piece?.glow(true)
    }

    private fun makeTurn(turn: Turn, newCell: CellView) {
        player.makeTurn(turn)
        chosenCell?.piece?.glow(false)
        movePiece(newCell)
        moves.clear()
    }

    private fun movePiece(newCell: CellView) {
        newCell.piece = chosenCell?.piece
        chosenCell?.piece = null
        chosenCell = null
        isPlayerTurn = false
    }

    private fun defineCorrectMove(curPos: Vector, newPos: Vector): Move? {
        return when(newPos - curPos) {
            Vector(1, 1) -> Move.MOVE_DOWN_RIGHT
            Vector(-1, 1) -> Move.MOVE_DOWN_LEFT
            Vector(1, -1) -> Move.MOVE_UP_RIGHT
            Vector(-1, -1) -> Move.MOVE_UP_LEFT
            Vector(2, 2) -> Move.ATTACK_DOWN_RIGHT
            Vector(-2, 2) -> Move.ATTACK_DOWN_LEFT
            Vector(2, -2) -> Move.ATTACK_UP_RIGHT
            Vector(-2, -2) -> Move.ATTACK_UP_LEFT
            else -> null
        }
    }
}