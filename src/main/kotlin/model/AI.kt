package model

import java.lang.Exception

class AI {
    fun makeTurn(board: Board, maximizingColor: Int): Turn? {
        //получаем все возможные ходы для данной ситуации на доске
        val availableTurns = board.getAvailableTurns()
        //храним ходы и определенную минимаксом конечную ценность доски, к которой они приведут
        val viewedTurns = mutableMapOf<Turn, Int>()
        for((key, value) in availableTurns) {
            for (moves in value) {
                board.makeTurn(Turn(key, moves))
                viewedTurns[Turn(key, moves)] = minimax(board, maximizingColor, DEPTH - 1)
                board.cancelLastTurn()
            }
        }
        //возвращаем самый оптимальный ход
        return viewedTurns.maxByOrNull { it.value }?.key ?: throw Exception("No turns left")
    }

    private fun minimax(board: Board, maximizingColor: Int, depth: Int): Int {
        if (depth == 0) {
            return board.cost
        }
        val color = board.turnsMade % 2

        val availableTurns = board.getAvailableTurns()
        //если ходов больше нет, то проигрывает тот, у кого нет ходов
        if (availableTurns.isEmpty()) return if (color == maximizingColor) Int.MIN_VALUE else Int.MAX_VALUE

        val viewedTurns = mutableListOf<Int>()
        for ((key, value) in availableTurns) {
            for (moves in value) {
                board.makeTurn(Turn(key, moves))
                //спускаемся ниже по рекурсии
                viewedTurns.add(minimax(board, maximizingColor, depth - 1))
                board.cancelLastTurn()
            }
        }

        return if (color == maximizingColor) viewedTurns.maxOf { it }
        else viewedTurns.minOf { it }
    }

    companion object {
        private const val DEPTH = 5
    }
}