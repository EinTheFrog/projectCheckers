package view

import javafx.geometry.Rectangle2D
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.shape.StrokeLineCap
import javafx.scene.shape.StrokeLineJoin
import javafx.scene.shape.StrokeType
import javafx.stage.Screen
import tornadofx.*
import java.awt.Color
import java.lang.IllegalArgumentException
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty

class Styles: Stylesheet() {
    companion object {
        // Define our styles
        val myMenu by cssclass()

        // Define our colors
        val darkAqua = c("#003C46")
        val lightMint = c("#AFFFEF")
        val ubuntuFont = loadFont("/Ubuntu-Regular.ttf", 14) ?: throw IllegalArgumentException("Invalid font path")
    }

    init {
        val screenSize = Screen.getPrimary().bounds
        val menuWidth = screenSize.width.px / 8
        val menuHeight = screenSize.height.px / 4
        val screenFontSize = screenSize.width.px / 50
        myMenu {
            padding = box(20.px)
            spacing = 40.px
            prefWidth = menuWidth
            prefHeight = menuHeight
            maxWidth = menuWidth
            maxHeight = menuHeight
            backgroundColor += lightMint
            borderColor += box(darkAqua)
            borderWidth += box(6.0.px)
        }

        button {
            font = ubuntuFont
            fontSize = screenFontSize
            textFill = darkAqua
            maxWidth = infinity
            backgroundColor += lightMint
            borderColor += box(darkAqua)
            borderWidth += box(6.0.px)
        }
    }
}