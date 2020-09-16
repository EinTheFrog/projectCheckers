package controller

import javafx.scene.effect.Bloom
import tornadofx.Controller
import tornadofx.add
import view.Cell
import view.Checker

class MyController: Controller() {
    private var checker: Checker? = null
    var checkersCell: Cell? = null
        set(value) {
            field = value
            checker = value?.checker
        }
    var isPlayerTurn = true
    fun chooseChecker(checkersCell: Cell) {
        if (!isPlayerTurn) return
        checker = checkersCell.checker
        checker!!.effect = Bloom()
        this.checkersCell = checkersCell
    }
    fun moveChecker(newCell: Cell) {
        if (checker == null) return
        newCell.add(checker!!)

        checkersCell = null
        isPlayerTurn = false
    }
}