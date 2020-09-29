package model

import java.lang.Exception
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Board(
        var turnsMade: Int,
        private val boardArray: Array<Array<Cell>> = Array(8) {Array(8) { Cell(null) } }
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
        val result = HashMap<Piece, MutableList<Move>>()
        for (i in boardArray.indices) {
            for (j in boardArray[i].indices) {
                if (boardArray[i][j].piece == null || boardArray[i][j].piece!!.color != turnsMade % 2) continue
                val piece = boardArray[i][j].piece ?: continue
                //просматриваем все возможные ходы фигуры
                for (move in Move.values()) {
                    val newBoardArray = boardArray.clone()
                    if (canPieceAttack(piece, move)) {
                        //если фигура может атаковать,
                        // то она обязана атаковать (а значит мы не можем просто переместить ее)
                        if (!result.containsKey(piece)) {
                            result[piece] = mutableListOf(move)
                        } else {
                            if (!result[piece]!!.last().isAttack) {
                                result[piece]!!.clear()
                            }
                            result[piece]!!.add(move)
                        }
                    } else if (newBoardArray.moveMyPiece(piece, move)) {
                        if (!result.containsKey(piece)) {
                            result[piece] = mutableListOf(move)
                        } else if (!result[piece]!!.last().isAttack) {
                            result[piece]!!.add(move)
                        }
                    }
                }
            }
        }
        return result
    }

    fun canPieceAttack(piece: Piece, move: Move): Boolean {
        val enemyPos = piece.pos + move.vector / 2
        val newPos = piece.pos + move.vector
        if (
                (piece.direction == move.direction ||
                piece.type == PieceType.KING) &&
                newPos.x in boardArray.indices &&
                newPos.y in boardArray[0].indices &&
                boardArray[enemyPos.x][enemyPos.y].piece != null &&
                boardArray[enemyPos.x][enemyPos.y].piece!!.color != piece.color &&
                boardArray[newPos.x][newPos.y].piece != null
        ) return true
        return false
    }

    fun canPieceMove(piece: Piece, move: Move): Boolean {
        val newPos = piece.pos + move.vector
        if (
                newPos.x !in boardArray.indices ||
                newPos.y !in boardArray[0].indices ||
                boardArray[newPos.x][newPos.y].piece != null
        ) return false
    }

    private fun Array<Array<Cell>>.movePiece(piece: Piece, move: Move) {
        if (move.isAttack) throw IllegalArgumentException("Can't move with attack move")
        val newPos = piece.pos + move.vector
        if (
                newPos.x !in boardArray.indices ||
                newPos.y !in boardArray[0].indices ||
                boardArray[newPos.x][newPos.y].piece != null
        ) return false

        this.changePiecePosition(piece.pos, newPos)

        return true
    }

    private fun Array<Array<Cell>>.attackEnemyPiece(piece: Piece, move: Move) {
        if (!move.isAttack) throw IllegalArgumentException("Can't attack with non attack move")
        if (!canPieceAttack(piece, move)) throw IllegalArgumentException("Can't attack like that")

        val enemyPos = piece.pos + move.vector / 2
        val newPos = piece.pos + move.vector

        this.changePiecePosition(piece.pos, newPos)
        this[enemyPos.x][enemyPos.y].piece = null
    }

    private fun Array<Array<Cell>>.changePiecePosition(oldPos: Vector, newPos: Vector) {
        val piece = this[oldPos.x][oldPos.y].piece ?: throw IllegalArgumentException("There is no piece at this position")

        this[oldPos.x][oldPos.y].piece = null
        this[newPos.x][newPos.y].piece = piece
        piece.pos = newPos
    }


    operator fun get(x: Int, y: Int) = boardArray[x][y]

    operator fun set(x: Int, y: Int, piece: Piece) {
        boardArray[x][y].piece = piece
    }
}