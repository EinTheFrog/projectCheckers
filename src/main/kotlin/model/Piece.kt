package model

data class Piece (
    val type: PieceType,
    var pos: Vector,
    val color: Int
)