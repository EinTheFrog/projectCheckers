package model

class AI {
    private val depth = 5
    fun makeTurn(board: Board, maximizingColor: Int): Turn {
        val availableTurns = board.getAvailableTurns()
        val viewedTurns = mutableMapOf<Int, Turn>()
        for (piece in availableTurns.keys) {
            for (moves in availableTurns[piece]!!) {
                val newBoard = board.clone()
                newBoard.makeTurn(Turn(piece, moves))
                viewedTurns[minimax(newBoard, maximizingColor, depth - 1)] = Turn(piece, moves)
            }
        }
        return viewedTurns[viewedTurns.keys.maxOf { it }]!!
    }

    private fun minimax(board: Board, maximizingColor: Int, depth: Int): Int {

        if (depth == 0) {
            return board.cost
        }
        val availableTurns = board.getAvailableTurns()
        val color = board.turnsMade % 2
        val viewedTurns = mutableListOf<Int>()
        for (piece in availableTurns.keys) {
            for (moves in availableTurns[piece]!!) {
                val newBoard = board.clone()
                newBoard.makeTurn(Turn(piece, moves))
                viewedTurns.add(minimax(newBoard, maximizingColor, depth - 1))
            }
        }

        return if (color == maximizingColor) viewedTurns.maxOf { it }
        else viewedTurns.minOf { it }
    }
}