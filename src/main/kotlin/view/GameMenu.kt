package view

import controller.MyController
import javafx.scene.input.KeyCode
import tornadofx.*

class GameMenu: View() {
    override val root = vbox {
        button {
            text = "Resume"
            setOnAction {
                parent.removeFromParent()
                find<MyController>().gameMode = MyController.GameMode.GAME
            }
        }
        button {
            text = "Main menu"
            setOnAction {
                parent.removeFromParent()
                find<GameView>().goToMainMenu()
            }
        }
    }

}