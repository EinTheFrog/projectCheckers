package model

import java.lang.Exception

class AI {
    fun makeTurn(board: Board, maximizingColor: Int): Turn {
        //получаем все возможные ходы для данной ситуации на доске
        val availableTurns = board.getAvailableTurns()
        //храним ходы и определенную минимаксом конечную ценность доски, к которой они приведут
        val viewedTurns = mutableMapOf<Turn, Int>()
        for((key, value) in availableTurns) {
            for (moves in value) {
                val piece = board.getPiece(key.pos)!!
                board.makeTurn(Turn(piece, moves))
                viewedTurns[Turn(key, moves)] = minimax(board, maximizingColor, DEPTH - 1)
            }
        }
        //возвращаем самый оптимальный ход
        return viewedTurns.maxByOrNull { it.value }?.key ?: throw Exception("No turns left")
    }

    private fun minimax(board: Board, maximizingColor: Int, depth: Int): Int {
        if (depth == 0) {
            return board.cost
        }
        var turnsMade = board.turns.size
        val color =  turnsMade % 2

        val availableTurns = board.getAvailableTurns()
        val viewedTurns = mutableListOf<Int>()
        for ((key, value) in availableTurns) {
            for (moves in value) {
                val piece = board.getPiece(key.pos)!!
                board.makeTurn(Turn(piece, moves), turnsMade)
                //спускаемся ниже по рекурсии
                viewedTurns.add(minimax(board, maximizingColor, depth - 1))
            }
        }

        return if (color == maximizingColor) viewedTurns.maxOf { it }
        else viewedTurns.minOf { it }
    }

    companion object {
        private const val DEPTH = 5
    }
}