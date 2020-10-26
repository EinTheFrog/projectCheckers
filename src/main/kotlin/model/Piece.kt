package model

data class Piece (
        val id: Int,
        var type: PieceType,
        var pos: Vector,
        val color: Int,
        val direction: Direction
)