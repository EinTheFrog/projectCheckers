package view

import javafx.stage.Screen
import tornadofx.*
import java.lang.IllegalArgumentException

class Styles: Stylesheet() {
    companion object {
        // Define our styles
        val myMenu by cssclass()
        val options by cssclass()

        // Define our colors
        val darkAqua = c("#003C46")
        val brightMint = c("#EBFFFB")
        val standardMint = c("#AFFFEF")
        val darkMint = c("33A58F")
        val dirtyMint = c("307A6C")
        val ubuntuFont = loadFont("/Ubuntu-Regular.ttf", 20) ?: throw IllegalArgumentException("Invalid font path")
    }

    val myVBox = mixin {
        padding = box(20.px)
        spacing = 40.px
        backgroundColor += standardMint
        borderColor += box(darkAqua)
        borderWidth += box(3.0.px)
    }

    val myText = mixin {
        font = ubuntuFont
        textFill = darkAqua
    }

    init {
        val screenSize = Screen.getPrimary().bounds
        val menuWidth = screenSize.width.px / 8
        val menuHeight = screenSize.height.px / 4
        myMenu {
            +myVBox
            prefWidth = menuWidth
            prefHeight = menuHeight
            maxWidth = menuWidth
            maxHeight = menuHeight
        }

        button {
            +myText
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

        label {
            +myText
        }

        options {
            +myVBox
            minWidth = menuWidth * 2
            prefHeight = menuHeight
        }
    }
}