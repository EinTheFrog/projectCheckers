package view

import controller.MyController
import javafx.scene.input.KeyCode
import model.Board
import tornadofx.*

class MyView: View() {
    private val controller = MyController()
    private val board = Board(0)
    override val root = borderpane {
        val pHeight = this.heightProperty()
        val pWidth = this.widthProperty()
        val boardView = BoardView(pHeight, pWidth, board) { cellView -> controller.clickOnCell(cellView) }
        controller.boardView = boardView
        center {
            add(boardView)
        }
        isFocusTraversable = false
        setOnKeyPressed {
            when(it.code) {
                KeyCode.ESCAPE -> controller.choosePiece(null)
                KeyCode.ENTER -> controller.isPlayerTurn = true
            }

        }
    }
}