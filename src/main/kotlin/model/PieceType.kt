package model

enum class PieceType {
    CHECKER {
        override val cost = 10
    },
    KING {
        override val cost = 12
    };
    public abstract val cost: Int
}