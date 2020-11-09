package controller

import javafx.concurrent.Task
import javafx.scene.input.KeyCode
import model.*
import tornadofx.Controller
import tornadofx.FXEvent
import view.*

class KeyEvent(val code: KeyCode): FXEvent()

class ClickEvent(val cell: CellView): FXEvent()

class MoveEvent(val pieceOldPos: Vector, val pieceNewPos: Vector): FXEvent()

class RemoveEvent(val piecePos: Vector): FXEvent()

object OpenMenuEvent: FXEvent()

object CloseMenuEvent: FXEvent()

object EndGameEvent: FXEvent()

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
        GAME, MENU, PAUSE
    }

    init {
        subscribe<KeyEvent> {
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
                    attackWithPiece(move)
                } else if (!board!!.getAvailableTurns().values.any{ it -> it.any{ it -> it.any{it.isAttack}}}){
                    movePiece(move)
                } else {
                    chosenPiece?.glow(false)
                    chosenPiece = null
                }
            }
        }
    }

    private fun onEsc() {
        when {
            chosenPiece != null -> choosePiece(null, null)
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
        //увеличиваем кол-во ходов, обнуляем выбранную фигуру и передаем ход другому игроку
        board!!.turnsMade++
        chosenPiece?.glow(false)
        chosenPiece = null
        isPlayerTurn = board!!.turnsMade % 2 == 0
    }

    private fun movePiece(move: Move) {
        if (chosenPiece == null) {
            throw IllegalStateException("Piece hasn't been set")
        }
        //убираем фигуру со старой клетки и ставим на новую
        board!!.move(board!![oldPos!!.x, oldPos!!.y].piece!!, move)
        fire(MoveEvent(oldPos!!, newPos!!))
        endTurn()
    }

    private fun attackWithPiece(attackMove: Move) {
        if (chosenPiece == null) {
            throw IllegalStateException("Piece hasn't been set")
        }
        board!!.attack(board!![oldPos!!.x, oldPos!!.y].piece!!, attackMove)
        fire(MoveEvent(oldPos!!, newPos!!))
        fire(RemoveEvent((oldPos!! + newPos!!)/2))
        if (!board!!.getAvailableMovesForPiece(board!![newPos!!.x, newPos!!.y].piece!!).any{it.first().isAttack}) {
            endTurn()
            return
        }
        oldPos = newPos!!.clone()
        newPos = null
    }

    private fun defineCorrectMove(curPos: Vector, newPos: Vector): Move? = Move.values().find { it.vector ==  newPos - curPos}

    private fun playAITurn() {
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
        for (i in aiTurn.moves.indices) {
            val move = aiTurn.moves[i]
            val mover = object: Task<Unit>() {
                override fun call() {
                    if (i > 0) {
                        gameMode = GameMode.PAUSE
                        Thread.sleep(500)
                        gameMode = GameMode.GAME
                    }
                }
            }
            mover.setOnSucceeded {
                newPos = Vector(aiTurn.piece.pos.x + move.vector.x, aiTurn.piece.pos.y + move.vector.y)
                if (move.isAttack) {
                    attackWithPiece(move)
                }
                else {
                    movePiece(move)
                }
            }
            Thread(mover).start()
        }

        if (board!!.getAvailableTurns().isEmpty()) {
            openLoseMenu()
            return
        }
    }
}