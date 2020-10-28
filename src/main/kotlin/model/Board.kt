package model

import java.lang.Exception
import java.lang.IndexOutOfBoundsException
import kotlin.IllegalArgumentException
import kotlin.collections.HashMap

class Board(
        var turnsMade: Int,
        private val boardArray: Array<Array<Cell>> = Array(8) {i -> Array(8) {j -> Cell(null, Vector(i, j)) } }
): Cloneable {
    private val turns = mutableListOf<Turn>()
    private val removedPieces = mutableListOf<Piece>()
    private val toKingCount = mutableMapOf<Int, Int>()

    fun getCost(maximizingColor: Int): Int {
        var result = 0
        for (i in boardArray.indices) {
            for (j in boardArray[i].indices) {
                val col = if (boardArray[i][j].piece?.color == maximizingColor) 1 else -1
                val locCost = boardArray[i][j].piece?.type?.cost ?: 0
                result += locCost * col
            }
        }
        return result
    }

    fun getAvailableTurns(): Map<Piece, List<List<Move>>> {
        val result = HashMap<Piece, List<List<Move>>>()
        for (i in boardArray.indices) {
            for (j in boardArray[i].indices) {
                //просматриваем все фигуры цвета ходящего
                if (boardArray[i][j].piece == null || boardArray[i][j].piece!!.color != turnsMade % 2) continue
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
        result.addAttacks(piece, setOf())
        if (result.isNotEmpty()) return result

        for (move in Move.values().filter { !it.isAttack }) {
            if (canPieceMove(piece, move)) {
                result.add(listOf(move))
            }
        }
        return result
    }

    private fun getAdditionalAttacks(piece: Piece, newPos: Vector, removedPieces: Set<Piece>): List<List<Move>> {
        val result = mutableListOf<List<Move>>()
        val phantomPiece = Piece(piece.id, piece.type, newPos, piece.color, piece.direction)
        result.addAttacks(phantomPiece, removedPieces)
        return result
    }

    private fun MutableList<List<Move>>.addAttacks(piece: Piece, removedPieces: Set<Piece>) {
        for (move in Move.values().filter { it.isAttack }) {
            //мы не можем менять доску при поиска возможных атак, поэтому вместо того, чтобы удалять фигуры при атаке
            //мы заносим их в set удаленных и при проверке последующих атак в комбинации не атакуем эти фигуры
            if (canPieceAttack(piece, move, removedPieces)) {
                val enemyPos = piece.pos + move.vector / 2
                val attackCombos = getAdditionalAttacks(
                        piece,
                        piece.pos + move.vector,
                        removedPieces + boardArray[enemyPos.x][enemyPos.y].piece!!
                )
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
    }

    fun canPieceMakeThisMove(piece: Piece, move: Move): Boolean {
        return if (move.isAttack) canPieceAttack(piece, move)
        else canPieceMove(piece, move)
    }

    private fun canPieceAttack(piece: Piece, move: Move, removedPieces: Set<Piece> = setOf()): Boolean {
        if (!move.isAttack) throw IllegalArgumentException("Can't attack with non attack move")

        val enemyPos = piece.pos + move.vector / 2
        val newPos = piece.pos + move.vector

        return (
                (piece.direction == move.direction || piece.type === PieceType.KING) &&
                        newPos.x in boardArray.indices &&
                        newPos.y in boardArray[0].indices &&
                        boardArray[enemyPos.x][enemyPos.y].piece != null &&
                        boardArray[enemyPos.x][enemyPos.y].piece !in removedPieces &&
                        boardArray[enemyPos.x][enemyPos.y].piece!!.color != piece.color &&
                        boardArray[newPos.x][newPos.y].piece == null
                )
    }

   private fun canPieceMove(piece: Piece, move: Move): Boolean {
       if (move.isAttack) throw IllegalArgumentException("Can't move with attack move")

        val newPos = piece.pos + move.vector
        return (
                (piece.direction == move.direction ||
                piece.type === PieceType.KING) &&
                newPos.x in boardArray.indices &&
                newPos.y in boardArray[0].indices &&
                boardArray[newPos.x][newPos.y].piece == null
                )
    }

    fun move(piece: Piece, move: Move) {
        if (!canPieceMove(piece, move)) {
            throw IllegalArgumentException("Can't move like that")
        }

        val newPos = piece.pos + move.vector
        changePiecePos(piece , newPos, true)
    }

    fun attack(piece: Piece, move: Move) {
        if (!canPieceAttack(piece, move)) {
            throw IllegalArgumentException("Can't attack like that")
        }

        val enemyPos = piece.pos + move.vector / 2
        val newPos = piece.pos + move.vector

        changePiecePos(piece, newPos, true)
        removedPieces.add(boardArray[enemyPos.x][enemyPos.y].piece!!)
        boardArray[enemyPos.x][enemyPos.y].piece = null
    }

    private fun changePiecePos(piece: Piece, newPos: Vector, needToCheckKing: Boolean = false) {
        val oldPos = piece.pos
        boardArray[oldPos.x][oldPos.y].piece = null
        boardArray[newPos.x][newPos.y].piece = piece
        piece.pos = newPos

        //шашка становится дамкой?
        if (needToCheckKing && didCheckerBecomeKing(piece)) {
            toKingCount[piece.id] = if (toKingCount.containsKey(piece.id)) toKingCount[piece.id]!! + 1 else 1
            piece.type = PieceType.KING
        }
    }

    fun makeTurn(turn: Turn) {
        //изменям расположение фигур на доске и кол-во совершенных ходов
        for (move in turn.moves) {
            if (move.isAttack) {
                attack(turn.piece, move)
            } else {
                move(turn.piece, move)
            }
        }
        turns.add(turn)
        turnsMade++
    }

    private fun didCheckerBecomeKing(piece: Piece) = (
            piece.pos.y == 0 && piece.direction == Direction.UP ||
            piece.pos.y == 7 && piece.direction == Direction.DOWN
            )

    fun cancelLastTurn() {
        val lastTurn = turns.last()
        turns.removeLast()

        for (moveInd in lastTurn.moves.size - 1 downTo  0) {
            val move = lastTurn.moves[moveInd]
            val newPos = lastTurn.piece.pos - move.vector

            if (didCheckerBecomeKing(lastTurn.piece)) {
                if (!toKingCount.containsKey(lastTurn.piece.id)) {
                    throw  IllegalArgumentException()
                }
                toKingCount[lastTurn.piece.id] = toKingCount[lastTurn.piece.id]!! - 1
                if (toKingCount[lastTurn.piece.id]!! == 0) {
                    toKingCount.remove(lastTurn.piece.id)
                    lastTurn.piece.type = PieceType.CHECKER
                }
            }

            changePiecePos(lastTurn.piece, newPos)

            if (move.isAttack) {
                val removedPiece = removedPieces.last()
                removedPieces.removeLast()
                boardArray[removedPiece.pos.x][removedPiece.pos.y].piece = removedPiece
            }
        }

        boardArray[lastTurn.piece.pos.x][lastTurn.piece.pos.y].piece = lastTurn.piece
        turnsMade--

        //debug
/*        if (toKingCount.containsKey(lastTurn.piece.id) && lastTurn.piece.type == PieceType.CHECKER) {
            val a = didCheckerBecomeKing(lastTurn.piece)
            val b = a
        }*/
    }

    //функции, упрощающие обращение к фигурам и клеткам
    operator fun get(x: Int, y: Int) = boardArray[x][y]

    operator fun set(x: Int, y: Int, piece: Piece) {
        boardArray[x][y].piece = piece
    }
}