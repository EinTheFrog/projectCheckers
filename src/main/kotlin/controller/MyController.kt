package controller

import javafx.concurrent.Task
import javafx.scene.input.KeyCode
import model.*
import tornadofx.Controller
import tornadofx.FXEvent
import view.*
import java.util.concurrent.TimeUnit

class KeyEvent(val code: KeyCode): FXEvent()

class ClickEvent(val cell: CellView): FXEvent()

class MoveEvent(val pieceOldPos: Vector, val pieceNewPos: Vector): FXEvent()

class RemoveEvent(val piecePos: Vector): FXEvent()

class ChoosePieceEvent(val pos: Vector?): FXEvent()

object OpenMenuEvent: FXEvent()

object CloseMenuEvent: FXEvent()

object EndGameEvent: FXEvent()

class MyController: Controller() {
    val board: Board = Board(0)
    var oldPos: Vector? = null
    var newPos: Vector? = null
    var isPlayerTurn = true
    private val ai = AI()
    var gameMode = GameMode.GAME
    private var hasChosenPiece = false

    enum class GameMode {
        GAME, MENU, PAUSE, PLAYER_ATTACK
    }

    init {
        subscribe<KeyEvent> {
            if (gameMode == GameMode.PAUSE) return@subscribe
            when (it.code) {
                KeyCode.ESCAPE -> onEsc()
                KeyCode.ENTER -> playAITurn()
            }
        }
        subscribe<ClickEvent>{
            clickOnCell(it.cell)
        }
    }

    //функция для обработки нажатия на клетку
    private fun clickOnCell(cell: CellView) {
        if (gameMode != GameMode.GAME && gameMode != GameMode.PLAYER_ATTACK) return

        //если сейчас ход ИИ, то игрок не может двигать фигуры
        if (!isPlayerTurn) return

        val piece = cell.piece
        if (
                piece != null &&
                board[cell.coords.x, cell.coords.y].piece!!.color == playerColorInd &&
                gameMode != GameMode.PLAYER_ATTACK
        ) {
            //если клетка не пустая и фигура нужного цвета, то выбираем фигуру
            choosePiece(cell.coords, true)
        } else if (hasChosenPiece) {//если игрок уже выбрал фигуру, то ходим ей
            //определяем какой ход хочет сделать игрок по взаимному расположению выбранной фигуры и нажатой клетки
            newPos = cell.coords
            val move = defineCorrectMove(oldPos!!, newPos!!)
            if (move != null && board.canPieceMakeThisMove(board[oldPos!!.x, oldPos!!.y].piece!!, move)) {
                if (move.isAttack) {
                    gameMode = GameMode.PLAYER_ATTACK
                    attackWithPiece(move, true)
                } else if (!board.getAvailableTurns().values.any{
                            turns -> turns.any{ moves -> moves.any{it.isAttack}}
                        }){
                    movePiece(move)
                } else {
                    choosePiece(null)
                }
            }
        }
    }

    private fun onEsc() {
        if (gameMode == GameMode.PLAYER_ATTACK) return
        when {
            hasChosenPiece -> choosePiece(null)
            gameMode == GameMode.GAME -> openMenu()
            gameMode == GameMode.MENU -> closeMenu()
        }
    }

    private fun openMenu() {
        fire(OpenMenuEvent)
        gameMode = GameMode.MENU
    }

    private fun openLoseMenu() {
        fire(EndGameEvent)
        gameMode = GameMode.MENU
    }

    private fun closeMenu() {
        fire(CloseMenuEvent)
        gameMode = GameMode.GAME
    }

    private fun choosePiece(pos: Vector?, highlightPiece: Boolean = false) {
        oldPos = pos
        newPos = null
        hasChosenPiece = pos != null
        if (highlightPiece) {
            fire(ChoosePieceEvent(pos))
        }
    }

    private fun endTurn() {
        board.turnsMade++
        choosePiece(null, true)
        isPlayerTurn = board.turnsMade % 2 == playerColorInd
    }

    private fun movePiece(move: Move) {
        board.move(board[oldPos!!.x, oldPos!!.y].piece!!, move)
        fire(MoveEvent(oldPos!!, newPos!!))
        endTurn()
    }

    private fun attackWithPiece(attackMove: Move, isPlayerAttack: Boolean) {
        gameMode = GameMode.PLAYER_ATTACK
        board.attack(board[oldPos!!.x, oldPos!!.y].piece!!, attackMove)
        fire(MoveEvent(oldPos!!, newPos!!))
        fire(RemoveEvent((oldPos!! + newPos!!) / 2))
        if (!board.getAvailableMovesForPiece(board[newPos!!.x, newPos!!.y].piece!!).any{it.first().isAttack}) {
            endTurn()
            gameMode = GameMode.GAME
            return
        }
        choosePiece(newPos, isPlayerAttack)
    }

    private fun defineCorrectMove(curPos: Vector, newPos: Vector): Move? = Move.values().find { it.vector ==  newPos - curPos}

    private fun playAITurn() {
        if (gameMode != GameMode.GAME) return

        gameMode = GameMode.PAUSE

        //определяем ход ИИ
        Thread {
            val aiTurn = ai.makeTurn(board!!)

            if (aiTurn == null) {
                openLoseMenu()
                return@Thread
            }

            //последовательно совершаем все ходы ИИ
            for (i in aiTurn.moves.indices) {
                val move = aiTurn.moves[i]
                if (i > 0) {
                    Thread.sleep(500)
                }
                val aiChosenPiecePos = Vector(aiTurn.piece.pos.x, aiTurn.piece.pos.y)
                choosePiece(aiChosenPiecePos)
                oldPos = aiTurn.piece.pos
                newPos = Vector(aiTurn.piece.pos.x + move.vector.x, aiTurn.piece.pos.y + move.vector.y)
                if (move.isAttack) {
                    attackWithPiece(move, false)
                } else {
                    movePiece(move)
                }
            }

            gameMode = GameMode.GAME
            if (board.getAvailableTurns().isEmpty()) {
                openLoseMenu()
            }
        }.start()

    }
}