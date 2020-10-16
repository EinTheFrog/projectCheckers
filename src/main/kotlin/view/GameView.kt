package view

import controller.MyController
import javafx.scene.input.KeyCode
import javafx.scene.layout.StackPane
import javafx.stage.Screen
import javafx.stage.StageStyle
import model.Board
import tornadofx.View
import tornadofx.pane
import tornadofx.replaceWith
import tornadofx.stackpane

class GameView: View() {
    //создаем контроллер и доску
    private val controller = find<MyController>()
    private var board = Board(mutableListOf())

    fun startNewGame() {
        board = Board(mutableListOf())
        root.replaceWith(createTable())
    }

    //добавляем корневой элемент
    override val root = createTable()

    fun goToMainMenu() {
        replaceWith(find<MainMenu>(), sizeToScene = true, centerOnScreen = true)
        currentWindow?.sizeToScene()
    }

    private fun createTable(): StackPane = stackpane {
        //передаем в boardView свойства размеров корневого элемента и событие,
        //которое должно выполнятся при нажатии на клетку
        val pHeight = this.heightProperty()
        val pWidth = this.widthProperty()
        var boardView = BoardView(pHeight, pWidth, board) { cellView -> controller.clickOnCell(cellView) }
        //передаем значение boardView контроллеру
        controller.boardView = boardView
        //добавляем boardView к корневому элементу
        add(boardView)
        //позволяем фокусироваться на корневом элементе
        isFocusTraversable = true
        //добавляем обработку нажатия клавиш
        setOnKeyPressed {
            when (it.code) {
                KeyCode.ESCAPE -> controller.onEsc()
                KeyCode.ENTER -> controller.playAITurn() // для передачи хода ИИ
                KeyCode.K -> controller.createKing()
            }
        }
        val gameWindowWidth = Screen.getPrimary().bounds.width / 2
        val gameWindowHeight = Screen.getPrimary().bounds.height / 9 * 8
        setMinSize(gameWindowWidth, gameWindowHeight)
    }
}