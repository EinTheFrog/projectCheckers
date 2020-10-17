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
                //для симуляции хода копируем доску
                val newBoard = board.clone()
                val piece = newBoard.getPiece(key.pos)!!
                newBoard.makeTurn(Turn(piece, moves))
                viewedTurns[Turn(key, moves)] = minimax(newBoard, maximizingColor, DEPTH - 1)
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
                //для симуляции хода копируем доску
                val newBoard = board.clone()
                val piece = newBoard.getPiece(key.pos)!!
                newBoard.makeTurn(Turn(piece, moves))
                //спускаемся ниже по рекурсии
                viewedTurns.add(minimax(newBoard, maximizingColor, depth - 1))
            }
        }

        return if (color == maximizingColor) viewedTurns.maxOf { it }
        else viewedTurns.minOf { it }
    }

    companion object {
        private const val DEPTH = 5
    }
}