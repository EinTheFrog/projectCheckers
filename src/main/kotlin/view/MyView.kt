package view
import javafx.stage.Screen
import tornadofx.*

class MyView: View() {
    override val root = find<MainMenu>().root

    fun startGame() {
        val gameWindowWidth = Screen.getPrimary().bounds.width / 2
        val gameWindowHeight = Screen.getPrimary().bounds.height / 9 * 8
        root.replaceWith(find<GameView>().root, sizeToScene = true, centerOnScreen = true)
        root.setMinSize(gameWindowWidth, gameWindowHeight)
        currentWindow?.sizeToScene()
    }

    fun goToMainMenu() {
        replaceWith<MainMenu>()
    }

    fun openGameMenu() {

    }
}