package view

import tornadofx.View
import tornadofx.button
import tornadofx.removeFromParent
import tornadofx.vbox

class LoseMenu: View() {
    override val root = vbox {
        button {
            text = "Main menu"
            setOnAction {
                parent.removeFromParent()
                find<GameView>().goToMainMenu()
            }
        }
    }
}