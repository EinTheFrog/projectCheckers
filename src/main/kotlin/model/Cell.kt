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

    override fun equals(other: Any?): Boolean {
        return (other is Cell && other.pos == pos)
    }

    override fun hashCode(): Int {
        return pos.hashCode() + 31 * piece.hashCode()
    }
}

