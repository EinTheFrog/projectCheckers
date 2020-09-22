package model

import java.lang.Exception
import java.lang.IllegalArgumentException

class Board(private val boardArray: Array<Array<Piece?>>, var turnsMade: Int) {
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
                            result.add(Board(newBoardArray, turnsMade + 1))
                        } else if (newBoardArray.moveMyPiece(piece, move)) {
                            result.add(Board(newBoardArray, turnsMade + 1))
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
            val enemyX = piece.x + move.x * 2
            val enemyY = piece.y + move.y * 2
            val newX = piece.x + move.x
            val newY = piece.y + move.y
            if (
                    newX !in boardArray[0].indices &&
                    newY !in boardArray.indices &&
                    boardArray[enemyX][enemyY] == null
                    &&  boardArray[enemyX][enemyY]!!.color != piece.color ||
                    boardArray[newX][newY] != null
            ) return true
        }
        return false
    }

    private fun Array<Array<Piece?>>.moveMyPiece(piece: Piece, move: Move): Boolean {
        val newX = piece.x + move.x
        val newY = piece.y + move.y

        if (
                newX !in boardArray[0].indices ||
                newY !in boardArray.indices ||
                boardArray[newX][newY] != null
        ) return false

        this.changePiecePosition(piece.x, piece.y, newX, newY)

        return true
    }

    private fun Array<Array<Piece?>>.attackEnemyPiece(piece: Piece, move: Move): Boolean {
        val enemyX = piece.x + move.x * 2
        val enemyY = piece.y + move.y * 2
        val newX = piece.x + move.x
        val newY = piece.y + move.y

        if (!canPieceAttack(piece)) return false

        this.changePiecePosition(piece.x, piece.y, newX, newY)
        this[enemyX][enemyY] = null

        attackEnemyPiece(piece, move)

        return true
    }

    private fun Array<Array<Piece?>>.changePiecePosition(oldX: Int, oldY: Int, newX: Int, newY: Int) {
        val piece = this[oldX][oldY] ?: throw IllegalArgumentException("There is no piece at this position")
        this[oldX][oldY] = null
        this[newX][newY] = piece
        piece.x = newX
        piece.y = newY
    }
}