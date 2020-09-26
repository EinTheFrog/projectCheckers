package controller

import javafx.scene.effect.Bloom
import javafx.scene.paint.Color
import model.Board
import model.Move
import model.Player
import model.Vector
import tornadofx.Controller
import tornadofx.add
import tornadofx.box
import tornadofx.style
import view.Cell
import view.Checker

class MyController(val board: Board): Controller() {
    val player = Player(board)
    var checkersCell: Cell? = null
    var isPlayerTurn = true

    fun chooseChecker(checkersCell: Cell?) {
        this.checkersCell?.checker?.glow(false)
        if (!isPlayerTurn || checkersCell?.checker == null) return

        this.checkersCell = checkersCell
        checkersCell.checker!!.glow(true)
    }

    fun makeMove(newCell: Cell) {
        val move = defineCorrectMove(checkersCell!!, newCell)
        if (checkersCell?.checker == null || move == null) return

        player.makeTurn(checkersCell!!.checker!!.piece, move)

        checkersCell!!.checker!!.glow(false)
        newCell.add(checkersCell!!.checker!!)
        checkersCell = null
        isPlayerTurn = false
    }

    private fun defineCorrectMove(curCell: Cell, newCell: Cell): Move? {
        return when(newCell.coords - curCell.coords) {
            Vector(1, 1) -> Move.UP_RIGHT
            Vector(-1, 1) -> Move.UP_LEFT
            Vector(1, -1) -> Move.DOWN_RIGHT
            Vector(-1, -1) -> Move.DOWN_LEFT
            else -> null
        }
    }
}