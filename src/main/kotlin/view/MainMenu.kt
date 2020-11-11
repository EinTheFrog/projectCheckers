package view

import controller.MyController
import tornadofx.*

class MainMenu: View() {
    override val root = vbox {
        addClass(Styles.myMenu)
        button {
            text = "Play"
            setOnAction {
                val gameView = find<GameView>()
                gameView.refresh()
                replaceWith(find<GameView>(), sizeToScene = true, centerOnScreen = true)
                currentWindow?.sizeToScene()
            }
        }
        button {
            text = "Options"
            setOnAction {
                replaceWith(find<OptionsMenu>(), sizeToScene = true, centerOnScreen = true)
                currentWindow?.sizeToScene()
            }
        }
    }

}