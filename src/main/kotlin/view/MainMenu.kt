package view

import tornadofx.*

class MainMenu: View() {
    override val root = vbox {
        addClass(Styles.myMenu)
        button {
            text = "Play"
            setOnAction { startGame() }
        }
    }

    private fun startGame() {
        replaceWith(find<GameView>(), sizeToScene = true, centerOnScreen = true)
        currentWindow?.sizeToScene()
    }
}