package model
open class Player(val board: Board) {
    fun makeTurn(piece: Piece, move: Move) {
        if (board.canPieceAttack(piece)) {
            board.attack(piece, move)
        } else {
            board.movePiece(piece, move)
        }
        board.turnsMade++
    }
}