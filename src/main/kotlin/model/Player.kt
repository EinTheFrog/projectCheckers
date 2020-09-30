package model
open class Player(private val board: Board) {
    fun makeTurn(turn: Turn) {
        if (turn.moves.first().isAttack) {
            //игрок может атаковать фигурой несколько раз подряд
            for (move in turn.moves) {
                board.attack(turn.piece, move)
            }
        } else {
            //игрок может двинуть фигуру только один раз
            board.move(turn.piece, turn.moves.first())
        }
        board.turnsMade++
    }
}