package model
open class Player(val board: Board) {
    fun makeTurn(turn: Turn) {
        if (board.canPieceAttack(turn.piece)) {
            //игрок может атаковать фигурой несколько раз подряд
            for (move in turn.moves) {
                board.attack(turn.piece, move)
            }
        } else {
            //игрок может двинуть фигуру только один раз подряд
            board.movePiece(turn.piece, turn.moves.first())
        }
        board.turnsMade++
    }
}