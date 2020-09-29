package view

import controller.MyController
import javafx.scene.input.KeyCode
import model.Board
import tornadofx.*

class MyView: View() {
    val board = Board(0)
    private val controller = MyController(board)
    override val root = borderpane {
        val pHeight = this.heightProperty()
        val pWidth = this.widthProperty()
        val board = BoardView(pHeight, pWidth, controller, board)
        center {
            add(board)
        }
        isFocusTraversable = false
        setOnKeyPressed {
            when(it.code) {
                KeyCode.ESCAPE -> controller.chooseCellWithPiece(null)
                KeyCode.ENTER -> controller.isPlayerTurn = true
            }

        }
    }
}