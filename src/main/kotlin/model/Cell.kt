package model

class Cell (piece: Piece?, val pos: Vector) {
    var piece: Piece? = null
    set(value) {
        field = value
        piece?.pos = pos
    }
    init {
        this.piece = piece
    }
}

