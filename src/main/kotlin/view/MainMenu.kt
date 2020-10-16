package view

import javafx.scene.Parent
import tornadofx.View
import tornadofx.button
import tornadofx.vbox

class MainMenu: View() {
    override val root = vbox {
        button {
            text = "Play"
            setOnAction { startGame() }
        }
    }

    private fun startGame() {
        replaceWith(find<GameView>(), sizeToScene = true, centerOnScreen = true)
        find<GameView>().startNewGame()
        currentWindow?.sizeToScene()
    }
}