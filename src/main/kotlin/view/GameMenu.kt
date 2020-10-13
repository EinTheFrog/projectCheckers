package view

import tornadofx.Fragment
import tornadofx.View
import tornadofx.button
import tornadofx.vbox

class GameMenu: View() {
    override val root = vbox {
        button {
            text = "Resume"
        }
        button {
            text = "Main menu"
        }
    }
}