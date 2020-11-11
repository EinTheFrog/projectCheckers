package view

import javafx.geometry.Pos
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import tornadofx.*
import java.lang.IllegalArgumentException

class OptionsMenu: View() {
    override val root = vbox {
        addClass(Styles.myMenu)
        addClass(Styles.options)

        lateinit var colorRec: Rectangle

        button {
            text = "Change player color"
            setOnAction {
                playerColorInd = (playerColorInd + 1) % 2
                colorRec.fill = if (playerColorInd == 1) Color.WHITE else Color.BLACK
            }
        }
        hbox {
            style {
                spacing = 10.px
            }
            label {
                text = "Current color:"
            }
            pane {
                colorRec = rectangle {
                    alignment = Pos.CENTER
                    height = 20.0
                    width = 20.0
                    fill = if (playerColorInd == 1) Color.WHITE else Color.BLACK
                }
            }
        }
        button {
            text = "Back"
            setOnAction {
                replaceWith(find<MainMenu>(), sizeToScene = true, centerOnScreen = true)
                currentWindow?.sizeToScene()
            }
        }
    }
}

public var playerColorInd = 0
    set(value) {
        if (value > 1 || value < 0) throw IllegalArgumentException("Illegal value for playerColorInd")
        field = value
    }