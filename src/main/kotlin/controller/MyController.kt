package controller

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import model.*
import tornadofx.Controller
import tornadofx.add
import tornadofx.removeFromParent
import view.*

class MyController: Controller() {
    private var board: Board? = null

    var boardView: BoardView? = null
    set(value) {
        field = value
        board = boardView?.board
    }

    var chosenPiece: PieceView? = null
    var oldPos: Vector? = null
    var newPos: Vector? = null
    var isPlayerTurn = true
    private val ai = AI()
    var gameMode = GameMode.GAME

    enum class GameMode {
        GAME, MENU
    }

    //функция для обработки нажатия на клетку
    fun clickOnCell(cell: CellView) {
        if (gameMode != GameMode.GAME) return
        //мы должны знать доску, на которой находится клетка
        if (boardView == null) {
            throw IllegalStateException("Board hasn't been set")
        }

        //если сейчас ход ИИ, то игрок не может двигать фигуры
        if (!isPlayerTurn) return

        val piece = cell.piece
        if (piece != null && board!![cell.coords.x, cell.coords.y].piece!!.color == 0) {
            //если клетка не пустая и фигура черная (пока игрок может играть только за черных), то выбираем фигуру
            choosePiece(piece, cell.coords)
        } else if (chosenPiece != null) {//если игрок уже выбрал фигуру, то ходим ей
            //определяем какой ход хочет сделать игрок по взаимному расположению выбранной фигуры и нажатой клетки
            newPos = cell.coords
            val move = defineCorrectMove(oldPos!!, newPos!!)
            if (move != null && board?.canPieceMakeThisMove(board!![oldPos!!.x, oldPos!!.y].piece!!, move) == true) {
                if (move.isAttack) {
                    attackWithPiece(cell, move)
                } else if (!board!!.getAvailableTurns().values.any{ it -> it.any{ it -> it.any{it.isAttack}}}){
                    movePiece(cell, move)
                    endTurn()
                } else {
                    chosenPiece?.glow(false)
                    chosenPiece = null
                }
            }
        }
    }

    fun onEsc() {
        when {
            chosenPiece != null -> choosePiece(null, null)
            gameMode == GameMode.GAME -> openMenu()
            else -> closeMenu()
        }
    }

    private fun openMenu() {
        find<GameView>().add(find<GameMenu>().root)
        gameMode = GameMode.MENU
    }

    private fun openLoseMenu() {
        find<GameView>().add(find<LoseMenu>().root)
        gameMode = GameMode.MENU
    }

    private fun closeMenu() {
        find<GameMenu>().removeFromParent()
        gameMode = GameMode.GAME
    }

    private fun choosePiece(piece: PieceView?, pos: Vector?) {
        oldPos = pos
        newPos = null
        chosenPiece?.glow(false)
        chosenPiece = piece
        chosenPiece?.glow(true)
    }

    private fun endTurn() {
        if (board == null) {
            throw IllegalStateException("Board hasn't been set")
        }
        //увеличиваем кол-во ходов, обнуляем выбранную фигуру
        board!!.turnsMade++
        if (board!![newPos!!.x, newPos!!.y].piece!!.type == PieceType.KING) {
            chosenPiece!!.becomeKing()
        }
        chosenPiece?.glow(false)
        chosenPiece = null
        isPlayerTurn = board!!.turnsMade % 2 == 0
    }

    private fun movePiece(newCell: CellView, move: Move) {
        if (chosenPiece == null) {
            throw IllegalStateException("Piece hasn't been set")
        }
        //убираем фигуру со старой клетки и ставим на новую
        boardView!![oldPos!!.x, oldPos!!.y] = null
        newCell.piece = chosenPiece
        board!!.move(board!![oldPos!!.x, oldPos!!.y].piece!!, move)
    }

    private fun attackWithPiece(newCell: CellView, attackMove: Move) {
        if (chosenPiece == null) {
            throw IllegalStateException("Piece hasn't been set")
        }
        val attackedCell = boardView!![(oldPos!!.x + newCell.coords.x) / 2, (oldPos!!.y + newCell.coords.y) / 2]
        //убираем с клетки атакованную фигуру
        attackedCell!!.piece = null
        //убираем фигуру со старой клетки и ставим на новую
        boardView!![oldPos!!.x, oldPos!!.y] = null
        newCell.piece = chosenPiece
        board!!.attack(board!![oldPos!!.x, oldPos!!.y].piece!!, attackMove)
        if (!board!!.getAvailableMovesForPiece(board!![newPos!!.x, newPos!!.y].piece!!).any{it.first().isAttack}) {
            endTurn()
        }
        oldPos = newPos!!.clone()
        newPos = null
    }

    private fun defineCorrectMove(curPos: Vector, newPos: Vector): Move? = Move.values().find { it.vector ==  newPos - curPos}

    fun playAITurn() {
        if (gameMode != GameMode.GAME) return
        if (board == null) {
            throw IllegalStateException("Board hasn't been set")
        }
        //определяем ход ИИ
        val aiTurn = ai.makeTurn(board!!, 1)

        if (aiTurn == null) {
            openLoseMenu()
            return
        }
        //выбираем фигуру по ходу ИИ
        chosenPiece = boardView!![aiTurn.piece.pos.x, aiTurn.piece.pos.y]!!.piece
        oldPos = aiTurn.piece.pos
        //последовательно совершаем все ходы ИИ
        for (move in aiTurn.moves) {
            if (aiTurn.moves.size > 1 && !move.isAttack) {
                val a = 0
            }
            val newCell = boardView!![aiTurn.piece.pos.x + move.vector.x, aiTurn.piece.pos.y + move.vector.y]!!
            newPos = newCell.coords
            if (move.isAttack) {
                attackWithPiece(newCell, move)
            }
            else {
                movePiece(newCell, move)
                endTurn()
            }
            runBlocking { launch { suspend { 100 } } }
        }

        if (board!!.getAvailableTurns().isEmpty()) {
            openLoseMenu()
            return
        }
    }
}