package model

import java.lang.Exception

class AI {
    private val depth = 5
    fun makeTurn(board: Board, maximizingColor: Int): Turn {
        val availableTurns = board.getAvailableTurns()
        val viewedTurns = mutableMapOf<Turn, Int>()
        for (pieceKey in availableTurns.keys) {
            for (moves in availableTurns[pieceKey]!!) {
                val newBoard = board.clone()
                val piece = newBoard.getPiece(pieceKey.pos)!!
                newBoard.makeTurn(Turn(piece, moves))
                viewedTurns[Turn(pieceKey, moves)] = minimax(newBoard, maximizingColor, depth - 1)
            }
        }
        return viewedTurns.maxByOrNull { it.value }?.key ?: throw Exception("No turns left")
    }

    private fun minimax(board: Board, maximizingColor: Int, depth: Int): Int {

        if (depth == 0) {
            return board.cost
        }
        val availableTurns = board.getAvailableTurns()
        val color = board.turnsMade % 2
        val viewedTurns = mutableListOf<Int>()
        for (pieceKey in availableTurns.keys) {
            for (moves in availableTurns[pieceKey]!!) {
                val newBoard = board.clone()
                val piece = newBoard.getPiece(pieceKey.pos)!!
                newBoard.makeTurn(Turn(piece, moves))
                viewedTurns.add(minimax(newBoard, maximizingColor, depth - 1))
            }
        }

        return if (color == maximizingColor) viewedTurns.maxOf { it }
        else viewedTurns.minOf { it }
    }
}