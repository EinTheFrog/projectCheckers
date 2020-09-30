package model

import java.lang.IllegalArgumentException
import kotlin.collections.HashMap

class Board(
        var turnsMade: Int,
        private val boardArray: Array<Array<Cell>> = Array(8) {i -> Array(8) {j -> Cell(null, Vector(i, j)) } }
) {
    val cost: Int
        get() {
            var result = 0
            for (i in boardArray.indices) {
                for (j in boardArray[i].indices) {
                    result += boardArray[i][j].piece?.type?.cost ?: 0
                }
            }
            return 0
        }

    fun getAvailableTurns(): Map<Piece, List<Move>> {
        val result = HashMap<Piece, List<Move>>()
        for (i in boardArray.indices) {
            for (j in boardArray[i].indices) {
                if (boardArray[i][j].piece == null || boardArray[i][j].piece!!.color != turnsMade % 2) continue
                val piece = boardArray[i][j].piece ?: continue
                val availableTurns = getAvailableMovesForPiece(piece)
                //если есть возможность атаковать, то мы должны атаковать
                if (result.values.any{ it -> it.any{!it.isAttack}}) {
                    if (availableTurns.any{it.isAttack}) {
                        result.clear()
                    }
                    result[piece] = availableTurns
                } else {
                    if (availableTurns.any{it.isAttack}) {
                        result[piece] = availableTurns
                    }
                }
            }
        }
        return result
    }

    fun getAvailableMovesForPiece(piece: Piece): List<Move> {
        val result = mutableListOf<Move>()
        //если фигура может атаковать,
        //то она обязана атаковать (а значит мы не можем просто переместить ее)
        for (move in Move.values().filter { it.isAttack }) {
            if (canPieceAttack(piece, move)) {
                result.add(move)
            }
        }
        if (result.isNotEmpty()) return result

        for (move in Move.values().filter { !it.isAttack }) {
            if (canPieceMove(piece, move)) {
                result.add(move)
            }
        }
        return result
    }

    fun canPieceMakeThisMove(piece: Piece, move: Move): Boolean {
        return if (move.isAttack) canPieceAttack(piece, move)
        else canPieceMove(piece, move)
    }

    private fun canPieceAttack(piece: Piece, move: Move): Boolean {
        if (!move.isAttack) throw IllegalArgumentException("Can't attack with non attack move")
        val enemyPos = piece.pos + move.vector / 2
        val newPos = piece.pos + move.vector
        if (
                newPos.x in boardArray.indices &&
                newPos.y in boardArray[0].indices &&
                boardArray[enemyPos.x][enemyPos.y].piece != null &&
                boardArray[enemyPos.x][enemyPos.y].piece!!.color != piece.color &&
                boardArray[newPos.x][newPos.y].piece == null
        ) return true
        return false
    }

   private fun canPieceMove(piece: Piece, move: Move): Boolean {
       if (move.isAttack) throw IllegalArgumentException("Can't move with attack move")
        val newPos = piece.pos + move.vector
        return !(
                (piece.direction != move.direction &&
                piece.type != PieceType.KING) ||
                newPos.x !in boardArray.indices ||
                newPos.y !in boardArray[0].indices ||
                boardArray[newPos.x][newPos.y].piece != null)
    }

    fun move(piece: Piece, move: Move) {
        if (!canPieceMove(piece, move)) throw IllegalArgumentException("Can't move like that")

        val newPos = piece.pos + move.vector
        changePiecePosition(piece.pos, newPos)
    }

    fun attack(piece: Piece, move: Move) {
        if (!canPieceAttack(piece, move)) throw IllegalArgumentException("Can't attack like that")

        val enemyPos = piece.pos + move.vector / 2
        val newPos = piece.pos + move.vector

        changePiecePosition(piece.pos, newPos)
        boardArray[enemyPos.x][enemyPos.y].piece = null
    }

    private fun changePiecePosition(oldPos: Vector, newPos: Vector) {
        val piece = boardArray[oldPos.x][oldPos.y].piece ?: throw IllegalArgumentException("There is no piece at this position")

        boardArray[oldPos.x][oldPos.y].piece = null
        boardArray[newPos.x][newPos.y].piece = piece
        piece.pos = newPos
    }


    operator fun get(x: Int, y: Int) = boardArray[x][y]

    operator fun set(x: Int, y: Int, piece: Piece) {
        boardArray[x][y].piece = piece
    }
}