package model

class Board(private val boardArray: Array<Array<Piece?>>) {
    val cost: Int
        get() {
            var result = 0
            for (i in boardArray.indices) {
                for (j in boardArray[i].indices) {
                    result += boardArray[i][j]?.type?.cost ?: 0
                }
            }
            return 0
        }

    fun getAvailableTurns(): List<Board> {
        val result = ArrayList<Board>()
        for (i in boardArray.indices) {
            for (j in boardArray[i].indices) {
                if (boardArray[i][j] != null) {
                    val piece = boardArray[i][j] ?: continue
                    for (move in piece.type.moves) {
                        val newBoardArray =
                                movePiece(piece, move) ?: continue
                        result.add(Board(newBoardArray))
                    }
                }
            }
        }
        return result
    }

    private fun movePiece(piece: Piece, move: Move): Array<Array<Piece?>>? {
        if (piece.x + move.x !in boardArray[0].indices ||
                piece.y + move.y !in boardArray.indices) return null
        val newBoard = boardArray.clone()
        newBoard[piece.x][piece.y] = null
        newBoard[piece.x + move.x][piece.y + move.y] = piece
        return newBoard
    }
}