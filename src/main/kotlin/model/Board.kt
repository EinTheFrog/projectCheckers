package model

import kotlin.IllegalArgumentException
import kotlin.collections.HashMap

class Board(
        var turnsMade: Int,
        private val boardArray: Array<Array<Cell>> = Array(8) {i -> Array(8) {j -> Cell(null, Vector(i, j)) } }
): Cloneable {
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
    private val turns = mutableListOf<Turn>()
    private val removedPieces = mutableListOf<Piece>()
    private val toKingCount = mutableMapOf<Piece, Int>()

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
        val phantomPiece = Piece(piece.type, newPos, piece.color, piece.direction)
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
        changePiecePosition(piece , newPos)
    }

    fun attack(piece: Piece, move: Move) {
        if (!canPieceAttack(piece, move)) {
            throw IllegalArgumentException("Can't attack like that")
        }

        val enemyPos = piece.pos + move.vector / 2
        val newPos = piece.pos + move.vector

        changePiecePosition(piece, newPos)
        removedPieces.add(boardArray[enemyPos.x][enemyPos.y].piece!!)
        boardArray[enemyPos.x][enemyPos.y].piece = null
    }

    private fun changePiecePosition(piece: Piece, newPos: Vector) {
        val oldPos = piece.pos

        boardArray[oldPos.x][oldPos.y].piece = null
        boardArray[newPos.x][newPos.y].piece = piece
        piece.pos = newPos

        //шашка становится дамкой?
        if (didCheckerBecomeKing(piece)) {
            toKingCount[piece] = if (toKingCount.containsKey(piece)) toKingCount[piece]!! + 1 else 1
            if (toKingCount[piece]!! > 1) {
                val a = 0
            }
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
        boardArray[lastTurn.piece.pos.x][lastTurn.piece.pos.y].piece = null
        for (move in lastTurn.moves) {
            if (didCheckerBecomeKing(lastTurn.piece)) {
                if (!toKingCount.containsKey(lastTurn.piece)) {
                    throw  IllegalArgumentException()
                }
                toKingCount[lastTurn.piece] = toKingCount[lastTurn.piece]!! - 1
                if (toKingCount[lastTurn.piece]!! == 0) {
                    toKingCount.remove(lastTurn.piece)
                    lastTurn.piece.type = PieceType.CHECKER
                }
            }

            lastTurn.piece.pos -= move.vector
            if (move.isAttack) {
                val removedPiece = removedPieces.last()
                removedPieces.removeLast()
                boardArray[removedPiece.pos.x][removedPiece.pos.y].piece = removedPiece
            }
        }
        boardArray[lastTurn.piece.pos.x][lastTurn.piece.pos.y].piece = lastTurn.piece
        turnsMade--
    }

    //функции, упрощающие обращение к фигурам и клеткам
    operator fun get(x: Int, y: Int) = boardArray[x][y]

    operator fun set(x: Int, y: Int, piece: Piece) {
        boardArray[x][y].piece = piece
    }

/*    public override fun clone(): Board {
        val boardArrayClone: Array<Array<Cell>> = Array(8) {i -> Array(8) {j -> Cell(null, Vector(i, j)) }}
        for (i in boardArray.indices) {
            for (j in boardArray[0].indices) {
                boardArrayClone[i][j] = boardArray[i][j].clone()
            }
        }
        return Board(turnsMade, boardArrayClone)
    }*/
}