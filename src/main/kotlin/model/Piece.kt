package model

class Piece (
    var type: PieceType,
    var pos: Vector,
    val color: Int,
    val direction: Direction
): Cloneable {

    override fun hashCode() = pos.x + 31 * pos.y

    override fun equals(other: Any?): Boolean = other is Piece && other.pos == pos && other.color == color

    public override fun clone(): Piece {
        return Piece(type, pos.clone(), color, direction)
    }

    override fun toString(): String {
        return "{Piece, type: $type, pos: $pos, color: $color, direction: $direction}"
    }
}