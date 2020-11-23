package model

class AI {
    fun makeTurn(board: Board): Turn? {
        val isMaximizing = board.turnsMade % 2 == MAXIMIZING_COLOR
        //получаем все возможные ходы для данной ситуации на доске
        val availableTurns = board.getAvailableTurns()
        //храним ходы и определенную ab-отсечением конечную ценность доски, к которой они приведут
        val viewedTurns = mutableMapOf<Turn, Int>()
        for((piece, turns) in availableTurns) {
            for (moves in turns) {
                board.makeTurn(Turn(piece, moves))
                viewedTurns[Turn(piece, moves)] = abSearch(board, isMaximizing, DEPTH - 1, -BIG_NUMBER, BIG_NUMBER)
                board.cancelLastTurn()
            }
        }
        //возвращаем самый оптимальный ход
        return if (isMaximizing) viewedTurns.maxByOrNull { it.value }?.key
        else viewedTurns.minByOrNull { it.value }?.key
    }

    private fun abSearch(board: Board, isMaximizing: Boolean, depth: Int, a: Int, b: Int): Int {
        if (depth == 0) {
            return board.getCost(MAXIMIZING_COLOR)
        }
        var a = a
        var b = b

        val availableTurns = board.getAvailableTurns()
        //если ходов больше нет, то проигрывает тот, у кого нет ходов
        if (availableTurns.isEmpty()) {
            val maximizingWinCost = BIG_NUMBER - board.turnsMade //чем быстрее победа, тем лучше
            return if (isMaximizing) -maximizingWinCost else maximizingWinCost
        }

        val listOfTurns = availableTurns.toList().toMutableList()
        if (availableTurns.any{pair -> pair.value.any{list -> list.any{it.isAttack}}}) {
            listOfTurns.sortBy { (_, turns) -> -turns.maxOf { it.size } }
        }

        var score = if (isMaximizing) -BIG_NUMBER else BIG_NUMBER
        outer@ for ((piece, turns) in listOfTurns) {
            for (moves in turns) {
                board.makeTurn(Turn(piece, moves))
                if (isMaximizing) {
                    score = maxOf(abSearch(board, !isMaximizing, depth - 1, a , b), score)
                    a = maxOf(a, score)
                } else {
                    score = minOf(abSearch(board, !isMaximizing, depth - 1, a , b), score)
                    b = minOf(b, score)
                }
                board.cancelLastTurn()
                if (b <= a) break@outer // если b <= а, то другой игрок не пойдет по данной ветке и дальше можно не смотреть
            }
        }
        return score
    }

    companion object {
        private const val DEPTH = 8
        private val BIG_NUMBER = 1024 * PieceType.KING.cost
        private const val MAXIMIZING_COLOR = 1
    }
}

