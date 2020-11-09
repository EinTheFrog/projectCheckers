package view

import tornadofx.*

class MainMenu: View() {
    override val root = vbox {
        addClass(Styles.wrapper)
        button {
            addClass(Styles.alice)
            text = "Play"
            setOnAction { startGame() }
        }
    }

    private fun startGame() {
        replaceWith(find<GameView>(), sizeToScene = true, centerOnScreen = true)
        currentWindow?.sizeToScene()
    }
}