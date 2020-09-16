package view

import controller.MyController
import javafx.beans.binding.DoubleBinding
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import tornadofx.Controller
import tornadofx.add
import tornadofx.gridpaneConstraints
import tornadofx.rectangle

class Cell(
        heightProperty: DoubleBinding,
        widthProperty: DoubleBinding,
        coords: Pair<Int, Int>,
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
            columnRowIndex(coords.second, coords.first)
        }
        setOnMouseClicked {
            if (checker != null) controller.chooseChecker(this)
            else controller.moveChecker(this)
        }
    }

    fun addChecker(checker: Checker) {
        this.checker = checker
        add(checker)
    }
}