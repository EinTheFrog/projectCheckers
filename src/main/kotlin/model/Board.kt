package model

import java.lang.Exception
import java.lang.IllegalArgumentException

class Board(var turnsMade: Int, val boardArray: Array<Array<Piece?>> = Array(8) {Array(8) {null} }) {
    val cost: Int
        get() {
            var result = 0
            for (i in boardArray.indices) {
                for (j in boardArray[i].indices) {
                    result += boardArray[i][j]?.type?.cost ?: 0
                }
            }
            return 0
        }

    fun makeAvailableTurns(): List<Board> {
        val result = ArrayList<Board>()
        for (i in boardArray.indices) {
            for (j in boardArray[i].indices) {
                if (boardArray[i][j] != null && boardArray[i][j]!!.color == turnsMade % 2) {
                    val piece = boardArray[i][j] ?: continue

                    for (move in piece.type.moves) {
                        val newBoardArray = boardArray.clone()
                        if (newBoardArray.attackEnemyPiece(piece, move)) {
                            result.add(Board(turnsMade + 1, newBoardArray))
                        } else if (newBoardArray.moveMyPiece(piece, move)) {
                            result.add(Board(turnsMade + 1, newBoardArray))
                        }
                    }
                }
            }
        }
        return result
    }

    fun movePiece(piece: Piece, move: Move) {
        if (canPieceAttack(piece)) {
            throw Exception("It is illegal to move piece when you can attack")
        }
        if (!boardArray.moveMyPiece(piece, move)) {
            throw IllegalArgumentException("Illegal move was provided to move piece")
        }
    }

    fun attack(piece: Piece, move: Move) {
        if (!boardArray.attackEnemyPiece(piece, move)) {
            throw IllegalArgumentException("Illegal move was provided to attack")
        }
    }

    fun canPieceAttack(piece: Piece): Boolean {
        for (move in piece.type.moves) {
            if (-1 !in boardArray.indices) println("its okay")
            val enemyPos = piece.pos + move.dir
            val newPos = piece.pos + move.dir * 2
            if (
                    newPos.y in boardArray.indices &&
                    newPos.x in boardArray[newPos.y].indices &&
                    boardArray[enemyPos.x][enemyPos.y] == null &&
                    boardArray[enemyPos.x][enemyPos.y]!!.color != piece.color ||
                    boardArray[newPos.x][newPos.y] != null
            ) return true
        }
        return false
    }

    private fun Array<Array<Piece?>>.moveMyPiece(piece: Piece, move: Move): Boolean {
        val newPos = piece.pos + move.dir
        if (
                newPos.x !in boardArray[0].indices ||
                newPos.y !in boardArray.indices ||
                boardArray[newPos.x][newPos.y] != null
        ) return false

        this.changePiecePosition(piece.pos, newPos)

        return true
    }

    private fun Array<Array<Piece?>>.attackEnemyPiece(piece: Piece, move: Move): Boolean {
        val enemyPos = piece.pos + move.dir * 2
        val newPos = piece.pos + move.dir * 2

        if (!canPieceAttack(piece)) return false

        this.changePiecePosition(piece.pos, newPos)
        this[enemyPos.x][enemyPos.y] = null

        attackEnemyPiece(piece, move)

        return true
    }

    private fun Array<Array<Piece?>>.changePiecePosition(oldPos: Vector, newPos: Vector) {
        val piece = this[oldPos.x][oldPos.y] ?: throw IllegalArgumentException("There is no piece at this position")
        this[oldPos.x][oldPos.y] = null
        this[newPos.x][newPos.y] = piece
        piece.pos = newPos
    }
}