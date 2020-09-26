package view

import controller.MyController
import javafx.scene.input.KeyCode
import model.Board
import tornadofx.*

class MyView: View() {
    private val controller: MyController by inject()
    override val root = borderpane {
        val pHeight = this.heightProperty()
        val pWidth = this.widthProperty()
        val board = BoardView(pHeight, pWidth, controller)
        board.addCheckers()
        center {
            add(board)
        }
        isFocusTraversable = false
        setOnKeyPressed {
            when(it.code) {
                KeyCode.ESCAPE -> controller.chooseChecker(null)
                KeyCode.ENTER -> controller.isPlayerTurn = true
            }

        }
    }
}