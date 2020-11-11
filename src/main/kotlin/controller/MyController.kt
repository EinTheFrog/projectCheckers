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
    var highLightedCells = listOf<CellView>()

    enum class GameMode {
        GAME, MENU, PAUSE
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
        if (gameMode != GameMode.GAME) return
        //мы должны знать доску, на которой находится клетка
        if (boardView == null) {
            throw IllegalStateException("Board hasn't been set")
        }

        //если сейчас ход ИИ, то игрок не может двигать фигуры
        if (!isPlayerTurn) return

        val piece = cell.piece
        if (piece != null && board!![cell.coords.x, cell.coords.y].piece!!.color == playerColorInd) {
            //если клетка не пустая и фигура нужного цвета, то выбираем фигуру
            choosePiece(piece, cell.coords)
        } else if (chosenPiece != null) {//если игрок уже выбрал фигуру, то ходим ей
            highLightForPlayer(false)
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

    private fun highLightCells(piece: Piece): List<CellView> {
        val highlightedCells = mutableListOf<CellView>()
        val availablePieceMoves = board!!.getAvailableMovesForPiece(piece)
        val allAvailableMoves = board!!.getAvailableTurns()

        val playerCanAttack = (allAvailableMoves.values.any{
                    moves -> moves.any{ moveList -> moveList.any{ it.isAttack} }
                })
        val currentPieceCanAttack = availablePieceMoves.any{moveList -> moveList.any{ it.isAttack}}
        if (playerCanAttack && !currentPieceCanAttack) return listOf()

        for (moveList in availablePieceMoves) {
            val move = moveList.first()
            val posAfterMove = getMoveCoords(move, piece.pos)
            boardView!![posAfterMove.x, posAfterMove.y]!!.highlight(true)
            highlightedCells.add( boardView!![posAfterMove.x, posAfterMove.y]!!)
        }
        return highlightedCells
    }

    private fun highLightForPlayer(bool: Boolean) {
        chosenPiece?.glow(bool)
        for (cell in highLightedCells) {
            cell.highlight(bool)
        }
    }

    private fun getMoveCoords(move: Move, curPos: Vector) = curPos + move.vector

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
        chosenPiece = null
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
        highLightForPlayer(false)
        chosenPiece = piece
        if (chosenPiece == null) return
        highLightedCells = highLightCells(board!![oldPos!!.x, oldPos!!.y].piece!!)
        highLightForPlayer(true)
    }

    private fun endTurn() {
        if (board == null) {
            throw IllegalStateException("Board hasn't been set")
        }
        //увеличиваем кол-во ходов, обнуляем выбранную фигуру и передаем ход другому игроку
        board!!.turnsMade++
        chosenPiece = null
        isPlayerTurn = board!!.turnsMade % 2 == playerColorInd
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
        fire(RemoveEvent((oldPos!! + newPos!!) / 2))
        if (!board!!.getAvailableMovesForPiece(board!![newPos!!.x, newPos!!.y].piece!!).any{it.first().isAttack}) {
            endTurn()
            return
        }
        oldPos = newPos!!.clone()
        newPos = null
    }

    private fun defineCorrectMove(curPos: Vector, newPos: Vector): Move? = Move.values().find { it.vector ==  newPos - curPos}

    private fun playAITurn() {
        highLightForPlayer(false)
        chosenPiece = null

        if (gameMode != GameMode.GAME) return

        gameMode = GameMode.PAUSE

        if (board == null) {
            throw IllegalStateException("Board hasn't been set")
        }
        //определяем ход ИИ
        Thread {
            val aiTurn = ai.makeTurn(board!!, 1)

            if (aiTurn == null) {
                openLoseMenu()
                return@Thread
            }

            //последовательно совершаем все ходы ИИ
            for (i in aiTurn.moves.indices) {
                val move = aiTurn.moves[i]
                if (i > 0) {
                    Thread .sleep(500)
                }
                chosenPiece = boardView!![aiTurn.piece.pos.x, aiTurn.piece.pos.y]!!.piece
                oldPos = aiTurn.piece.pos
                newPos = Vector(aiTurn.piece.pos.x + move.vector.x, aiTurn.piece.pos.y + move.vector.y)
                if (move.isAttack) {
                    attackWithPiece(move)
                } else {
                    movePiece(move)
                }
            }

            gameMode = GameMode.GAME
            if (board!!.getAvailableTurns().isEmpty()) {
                openLoseMenu()
            }
        }.start()

    }
}