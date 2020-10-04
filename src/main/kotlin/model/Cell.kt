package model

class Cell (piece: Piece?, val pos: Vector): Cloneable {
    var piece: Piece? = null
    set(value) {
        field = value
        piece?.pos = pos
    }
    init {
        this.piece = piece
    }

    public override fun clone(): Cell {
        return Cell(piece?.clone(), pos.clone())
    }
}

