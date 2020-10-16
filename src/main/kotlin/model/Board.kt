package model

import java.lang.IllegalArgumentException
import kotlin.collections.HashMap

class Board(
        var turns: MutableList<Turn>,
        private val boardsInTime: MutableMap<Int, Array<Array<Cell>>> =
                mutableMapOf(0 to Array(8) {i -> Array(8) {j -> Cell(null, Vector(i, j)) } })
) {
    val cost: Int
        get() {
            var result = 0
            val boardArray =
                    boardsInTime[turns.size] ?: throw IllegalArgumentException("Hasn't made such many turns")
            for (i in boardArray.indices) {
                for (j in boardArray[i].indices) {
                    result += boardArray[i][j].piece?.type?.cost ?: 0
                }
            }
            return 0
        }

    fun getAvailableTurns(): Map<Piece, List<List<Move>>> {
        val result = HashMap<Piece, List<List<Move>>>()
        val boardArray =
                boardsInTime[turns.size] ?: throw IllegalArgumentException("Hasn't made such many turns")
        for (i in boardArray.indices) {
            for (j in boardArray[i].indices) {
                //просматриваем все фигуры цвета ходящего
                if (boardArray[i][j].piece == null || boardArray[i][j].piece!!.color != turns.size % 2) continue
                val piece = boardArray[i][j].piece ?: continue
                val availableTurns = getAvailableMovesForPiece(piece)
                if (availableTurns.isEmpty()) continue
                //если есть возможность атаковать, то мы должны атаковать
                if (result.values.any{ it -> it.any{it.first().isAttack}}) {
                    if (availableTurns.any{it.first().isAttack}) {
                        result[piece] = availableTurns
                    }
                } else {
                    if (availableTurns.any{it.first().isAttack}) {
                        result.clear()
                    }
                    result[piece] = availableTurns
                }
            }
        }
        return result
    }

    fun getAvailableMovesForPiece(piece: Piece): List<List<Move>> {
        val result = mutableListOf<List<Move>>()
        //если фигура может атаковать,
        //то она обязана атаковать (а значит мы не можем просто переместить ее)
        result.addAttacks(piece)
        if (result.isNotEmpty()) return result

        for (move in Move.values().filter { !it.isAttack }) {
            if (canPieceMove(piece, move)) {
                result.add(listOf(move))
            }
        }
        return result
    }

/*    private fun getAdditionalAttacks(piece: Piece): List<List<Move>> {
        val result = mutableListOf<List<Move>>()
        //val phantomPiece = Piece(piece.type, newPos, piece.color, piece.direction)
        result.addAttacks(phantomPiece)
        return result
    }*/

    private fun MutableList<List<Move>>.addAttacks(piece: Piece): List<List<Move>>  {
        val newBoard =
                boardsInTime[turns.size]?.clone() ?: throw IllegalArgumentException("Hasn't made such many turns")
        //мы еще не знаем сколько атак будет в ходе, поэтому мы не добавляем ход в turns,
        //однако нам нужно двигать фигуры (для определения кол-ва возможных атак), не портя нынешнюю доску
        boardsInTime[turns.size + 1] = newBoard
        for (move in Move.values().filter { it.isAttack }) {
            if (canPieceAttack(piece, move)) {
                attack(piece, move)
                val attackCombos =  mutableListOf<List<Move>>().addAttacks(piece)
                if (attackCombos.isNotEmpty()) {
                    for (attackCombo in attackCombos) {
                        val attacks = mutableListOf(move)
                        attacks.addAll(attackCombo)
                        this.add(attacks)
                    }
                } else {
                    this.add(listOf(move))
                }
            }
        }
        //удаляем виртуальную доску
        boardsInTime.remove(turns.size + 1)
        return this
    }

    fun canPieceMakeThisMove(piece: Piece, move: Move): Boolean {
        return if (move.isAttack) canPieceAttack(piece, move)
        else canPieceMove(piece, move)
    }

    private fun canPieceAttack(piece: Piece, move: Move): Boolean {
        if (!move.isAttack) throw IllegalArgumentException("Can't attack with non attack move")

        val enemyPos = piece.pos + move.vector / 2
        val newPos = piece.pos + move.vector
        val boardArray =
                boardsInTime[turns.size] ?: throw IllegalArgumentException("Hasn't made such many turns")
        val result =  (
                (piece.direction == move.direction || piece.type === PieceType.KING) &&
                newPos.x in boardArray.indices &&
                newPos.y in boardArray[0].indices &&
                boardArray[enemyPos.x][enemyPos.y].piece != null &&
                boardArray[enemyPos.x][enemyPos.y].piece!!.color != piece.color &&
                boardArray[newPos.x][newPos.y].piece == null
                )
        if (piece.type == PieceType.KING && result) {
            val a = 0
        }
        return result
    }

   private fun canPieceMove(piece: Piece, move: Move): Boolean {
       if (move.isAttack) throw IllegalArgumentException("Can't move with attack move")

        val newPos = piece.pos + move.vector
       val boardArray =
               boardsInTime[turns.size] ?: throw IllegalArgumentException("Hasn't made such many turns")
        return (
                (piece.direction == move.direction ||
                piece.type === PieceType.KING) &&
                newPos.x in boardArray.indices &&
                newPos.y in boardArray[0].indices &&
                boardArray[newPos.x][newPos.y].piece == null
                )
    }

    fun makeTurn(turn: Turn, turnNumber: Int = turns.size + 1) {
        turns = turns.subList(0, turnNumber - 1)
        turns.add(turn)
        val newBoard =
                boardsInTime[turns.size]?.clone() ?: throw IllegalArgumentException("Hasn't made such many turns")
        boardsInTime[turns.size] = newBoard
        //изменям расположение фигур на доске и кол-во совершенных ходов
        for (move in turn.moves) {
            if (move.isAttack) {
                attack(turn.piece, move)
            } else {
                move(turn.piece, move)
            }
        }
    }

    private fun move(piece: Piece, move: Move) {
        if (!canPieceMove(piece, move)) {
            throw IllegalArgumentException("Can't move like that")
        }

        val newPos = piece.pos + move.vector
        changePiecePosition(piece , newPos)
    }

    private fun attack(piece: Piece, move: Move) {
        if (!canPieceAttack(piece, move)) {
            throw IllegalArgumentException("Can't attack like that")
        }

        val enemyPos = piece.pos + move.vector / 2
        val newPos = piece.pos + move.vector

        changePiecePosition(piece, newPos)
        val boardArray =
                boardsInTime[turns.size] ?: throw IllegalArgumentException("Hasn't made such many turns")
        boardArray[enemyPos.x][enemyPos.y].piece = null
    }

    private fun changePiecePosition(piece: Piece, newPos: Vector) {
        val oldPos = piece.pos

        val boardArray =
                boardsInTime[turns.size] ?: throw IllegalArgumentException("Hasn't made such many turns")
        boardArray[oldPos.x][oldPos.y].piece = null
        boardArray[newPos.x][newPos.y].piece = piece
        piece.pos = newPos
    }

    //функции, упрощающие обращение к фигурам и клеткам
    fun getPiece(pos: Vector): Piece? {
        val boardArray =
                boardsInTime[turns.size] ?: throw IllegalArgumentException("Hasn't made such many turns")
        return boardArray[pos.x][pos.y].piece
    }

    operator fun get(x: Int, y: Int): Cell {
        val boardArray =
                boardsInTime[turns.size] ?: throw IllegalArgumentException("Hasn't made such many turns")
        return boardArray[x][y]
    }

    operator fun set(x: Int, y: Int, piece: Piece) {
        val boardArray =
                boardsInTime[turns.size] ?: throw IllegalArgumentException("Hasn't made such many turns")
        boardArray[x][y].piece = piece
    }

    fun Array<Array<Cell>>.clone(): Array<Array<Cell>> {
        val boardArrayClone: Array<Array<Cell>> = Array(8) {i -> Array(8) {j -> Cell(null, Vector(i, j)) }}
        for (i in this.indices) {
            for (j in this[0].indices) {
                boardArrayClone[i][j] = this[i][j].clone()
            }
        }
        return boardArrayClone
    }
}