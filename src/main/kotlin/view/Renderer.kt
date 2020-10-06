package view

import javafx.scene.Parent
import javafx.scene.image.ImageView
import javafx.scene.layout.GridPane
import javafx.scene.paint.Color
import javafx.scene.shape.Ellipse
import javafx.scene.shape.Rectangle
import model.Board
import model.Cell
import tornadofx.*
import java.awt.image.BufferedImage

class Renderer {
    fun render(boardArray: Array<Array<Cell>>): BufferedImage {
        val img = BufferedImage(8, 8, BufferedImage.TYPE_INT_RGB)
        val graphics = img.graphics
        graphics.color = java.awt.Color.GRAY
        graphics.fillRect(0, 0, 8, 8)


        for (i in boardArray.indices) {
            for (j in boardArray[i].indices) {
                if (boardArray[i][j].piece?.color == 0) {
                    img.setRGB(i, j, 255)
                }
                if (boardArray[i][j].piece?.color == 1) {
                    img.setRGB(i, j, 0)
                }
            }
        }

        return img
    }
}