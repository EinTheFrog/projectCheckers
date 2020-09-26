package view

import controller.MyController
import javafx.beans.binding.DoubleBinding
import javafx.scene.Node
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import model.Vector
import tornadofx.*

class Cell(
        heightProperty: DoubleBinding,
        widthProperty: DoubleBinding,
        val coords: Vector,
        color: Color,
        controller: MyController
): StackPane() {
    var checker: Checker? = null
    init {
        isFocusTraversable = true
        rectangle {
            this.heightProperty().bind(heightProperty)
            this.widthProperty().bind(widthProperty)
            fill = color
        }
        gridpaneConstraints {
            columnRowIndex(coords.x, coords.y)
        }
        setOnMouseClicked {
            if (checker != null) controller.chooseChecker(this)
            else controller.makeMove(this)
        }
    }

    fun add(checker: Checker) {
        this.checker = checker
        add(checker as Node)
    }
}