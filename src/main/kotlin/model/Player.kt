package model

class Player {
    fun makeTurn(board: Board, piece: Piece, move: Move) {
        if (board.canPieceAttack(piece)) {
            board.attack(piece, move)
        } else {
            board.movePiece(piece, move)
        }
        board.turnsMade++
    }
}