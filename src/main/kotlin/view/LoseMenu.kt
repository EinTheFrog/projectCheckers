package view

import tornadofx.*

class LoseMenu: View() {
    override val root = vbox {
        addClass(Styles.myMenu)
        button {
            text = "Main menu"
            setOnAction {
                parent.removeFromParent()
                find<GameView>().goToMainMenu()
            }
        }
    }
}