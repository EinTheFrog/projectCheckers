package model

class Board {
    private val boardArray = Array(8) { Array<Piece?>(8) { null } }
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

    fun getAvailableTurns(): Board {
        val availableTurns = HashMap<Piece, ArrayList<Move>>()
        for (i in boardArray.indices) {
            for (j in boardArray[i].indices) {
                if (boardArray[i][j] != null) {
                    val piece = boardArray[i][j]
                }
            }
        }
    }

    private fun doesCellExist(): Boolean {
        
        return false
    }
}