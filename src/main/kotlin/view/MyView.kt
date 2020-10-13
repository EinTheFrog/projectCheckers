package view
import tornadofx.*

class MyView: View() {
    override val root = borderpane { center<MainMenu>() }

    fun startGame() {
        root.center<GameView>()
    }

    fun goToMainMenu() {
        replaceWith<MainMenu>()
    }
}