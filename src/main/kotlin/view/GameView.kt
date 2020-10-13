package view

import controller.MyController
import javafx.scene.input.KeyCode
import model.Board
import tornadofx.View
import tornadofx.pane

class GameView: View() {
    //создаем контроллер и доску
    private val controller = MyController()
    private val board = Board(0)

    //добавляем корневой элемент
    override val root = pane {
        //передаем в boardView свойства размеров корневого элемента и событие,
        //которое должно выполнятся при нажатии на клетку
        val pHeight = this.heightProperty()
        val pWidth = this.widthProperty()
        val boardView = BoardView(pHeight, pWidth, board) { cellView -> controller.clickOnCell(cellView) }
        //передаем значение boardView контроллеру
        controller.boardView = boardView
        //добавляем boardView к корневому элементу
        add(boardView)
        //позволяем фокусироваться на корневом элементе
        isFocusTraversable = false
        //добавляем обработку нажатия клавиш
        setOnKeyPressed {
            when (it.code) {
                KeyCode.ESCAPE -> controller.choosePiece(null) //для отмены выбора фигуры
                KeyCode.ENTER -> controller.playAITurn() // для передачи хода ИИ
            }
        }
    }
}