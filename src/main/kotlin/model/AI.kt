package model

import java.lang.Exception

class AI {
    fun makeTurn(board: Board, maximizingColor: Int): Turn? {
        //получаем все возможные ходы для данной ситуации на доске
        val availableTurns = board.getAvailableTurns()
        //храним ходы и определенную минимаксом конечную ценность доски, к которой они приведут
        val viewedTurns = mutableMapOf<Turn, Int>()
        for((piece, turns) in availableTurns) {
            for (moves in turns) {
                board.makeTurn(Turn(piece, moves))
                viewedTurns[Turn(piece, moves)] = minimax(board, maximizingColor, DEPTH - 1, -BIG_NUMBER, BIG_NUMBER)
                board.cancelLastTurn()
            }
        }
        //возвращаем самый оптимальный ход
        val isMaximizing = board.turnsMade % 2 == maximizingColor
        return if (isMaximizing) viewedTurns.maxByOrNull { it.value }?.key
        else viewedTurns.minByOrNull { it.value }?.key
    }

    private fun minimax(board: Board, maximizingColor: Int, depth: Int, a: Int, b: Int): Int {
        if (depth == 0) {
            return board.getCost(maximizingColor)
        }
        val color = board.turnsMade % 2
        val isMaximizing = color == maximizingColor
        var a = a
        var b = b

        val availableTurns = board.getAvailableTurns()
        //если ходов больше нет, то проигрывает тот, у кого нет ходов
        if (availableTurns.isEmpty()) {
            val maxWinCost = BIG_NUMBER - board.turnsMade //чем быстрее победа, тем лучше
            return if (isMaximizing) -maxWinCost else maxWinCost
        }

        //val viewedTurns = mutableListOf<Int>()
        var result = if (isMaximizing) -BIG_NUMBER else +BIG_NUMBER
        for ((piece, turns) in availableTurns) {
            for (moves in turns) {
                board.makeTurn(Turn(piece, moves))
                result = maxOf(minimax(board, maximizingColor, depth - 1, a , b), result)
                board.cancelLastTurn()

                if (isMaximizing) {
                    a = maxOf(a, result)
                } else {
                    b = minOf(b, result)
                }
                if (b <= a) break // если b<=а, то другой игрок не пойдет по данной ветке и дальше можно не смотреть
            }
        }

        return result
    }

    companion object {
        private const val DEPTH = 7
        private val BIG_NUMBER = 1024 * PieceType.KING.cost
    }
}

