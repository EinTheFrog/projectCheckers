package model

data class Piece (
    var type: PieceType,
    var pos: Vector,
    val color: Int,
    val direction: Direction
): Cloneable {
    public override fun clone(): Piece {
        return Piece(type, pos.clone(), color, direction)
    }
}