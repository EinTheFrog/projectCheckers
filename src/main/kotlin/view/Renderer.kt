package view

import model.Cell
import java.awt.Color
import java.awt.image.BufferedImage

/**
 * Класс для упрощения процесса поиска багов
 */
class Renderer {
    fun render(boardArray: Array<Array<Cell>>): BufferedImage {
        val img = BufferedImage(8, 8, BufferedImage.TYPE_INT_RGB)
        val graphics = img.graphics
        graphics.color = Color.GRAY
        graphics.fillRect(0, 0, 8, 8)


        for (i in boardArray.indices) {
            for (j in boardArray[i].indices) {
                if ((i + j) % 2 == 0) {
                    img.setRGB(i, j, 65536 * 100 + 256 * 100 + 100 )
                }
                if (boardArray[i][j].piece?.color == 1) {
                    img.setRGB(i, j, 65536 * 255 + 256 * 255 + 255)
                }
                if (boardArray[i][j].piece?.color == 0) {
                    img.setRGB(i, j, 0)
                }
            }
        }

        return img
    }
}