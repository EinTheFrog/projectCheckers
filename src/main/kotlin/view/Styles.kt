package view

import javafx.stage.Screen
import tornadofx.*
import java.lang.IllegalArgumentException

class Styles: Stylesheet() {
    companion object {
        // Define our styles
        val myMenu by cssclass()

        // Define our colors
        val darkAqua = c("#003C46")
        val brightMint = c("#EBFFFB")
        val standardMint = c("#AFFFEF")
        val darkMint = c("33A58F")
        val dirtyMint = c("307A6C")
        val ubuntuFont = loadFont("/Ubuntu-Regular.ttf", 20) ?: throw IllegalArgumentException("Invalid font path")
    }

    init {
        val screenSize = Screen.getPrimary().bounds
        val menuWidth = screenSize.width.px / 8
        val menuHeight = screenSize.height.px / 4
        myMenu {
            padding = box(20.px)
            spacing = 40.px
            prefWidth = menuWidth
            prefHeight = menuHeight
            maxWidth = menuWidth
            maxHeight = menuHeight
            backgroundColor += standardMint
            borderColor += box(darkAqua)
            borderWidth += box(3.0.px)
        }

        button {
            font = ubuntuFont
            textFill = darkAqua
            maxWidth = infinity
            backgroundColor += standardMint
            borderColor += box(darkAqua)
            borderWidth += box(3.0.px)
            and(hover) {
                backgroundColor += darkMint
                textFill = brightMint
            }
            and(pressed) {
                backgroundColor += dirtyMint
                textFill = brightMint
            }
        }
    }
}