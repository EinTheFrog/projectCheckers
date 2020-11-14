package view

import controller.*
import javafx.scene.input.KeyCode
import javafx.scene.layout.StackPane
import javafx.stage.Screen
import model.Board
import tornadofx.View
import tornadofx.stackpane

class GameView: View() {
    //создаем контроллер и доску
    private val controller = find<MyController>()
    lateinit var boardView: BoardView

    //добавляем корневой элемент
    override val root = createTable()
    init {
        subscribe<OpenMenuEvent> {
            add(find<GameMenu>().root)
        }
        subscribe<CloseMenuEvent> {
            root.children.remove(find<GameMenu>().root)
        }
        subscribe<EndGameEvent> {
            add(find<LoseMenu>().root)
        }
    }

    fun refresh() {
        controller.isPlayerTurn = playerColorInd == 0
        boardView.refresh()
        controller.gameMode = MyController.GameMode.GAME
    }

    fun goToMainMenu() {
        replaceWith(find<MainMenu>(), sizeToScene = true, centerOnScreen = true)
        currentWindow?.sizeToScene()
    }

    private fun createTable(): StackPane = stackpane {
        //передаем в boardView свойства размеров корневого элемента и событие,
        //которое должно выполнятся при нажатии на клетку
        val pHeight = this.heightProperty()
        val pWidth = this.widthProperty()
        boardView = BoardView(pHeight, pWidth, controller.board) { cellView -> fire(ClickEvent(cellView)) }
        //добавляем boardView к корневому элементу
        add(boardView)
        //позволяем фокусироваться на корневом элементе
        isFocusTraversable = true
        //добавляем обработку нажатия клавиш
        setOnKeyPressed {
            if (it.code == KeyCode.ESCAPE || it.code == KeyCode.ENTER) {
                fire(KeyEvent(it.code))
            }
        }
        val gameWindowWidth = Screen.getPrimary().bounds.width / 2
        val gameWindowHeight = Screen.getPrimary().bounds.height / 9 * 8
        setMinSize(gameWindowWidth, gameWindowHeight)

        subscribe<MoveEvent> {
            boardView.changePiecePos(it.pieceOldPos, it.pieceNewPos)
        }
        subscribe<RemoveEvent> {
            boardView.removePiece(it.piecePos)
        }
        subscribe<ChoosePieceEvent> {
            boardView.choosePieceView(it.pos)
        }
    }
}