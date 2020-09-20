package model

enum class PieceType {
    CHECKER {
        override val moves = listOf(Move.UP_LEFT, Move.UP_RIGHT)
        override val cost = 10
    },
    KING {
        override val moves: List<Move> = listOf(Move.UP_LEFT, Move.UP_RIGHT, Move.DOWN_RIGHT, Move.DOWN_LEFT)
        override val cost = 12
    };
    public abstract val moves: List<Move>
    public abstract val cost: Int
}