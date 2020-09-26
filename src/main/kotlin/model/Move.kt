package model

enum class Move {
    UP_LEFT {
        override val dir = Vector(-1, 1)
    },
    UP_RIGHT {
        override val dir = Vector(1, 1)
    },
    DOWN_RIGHT{
        override val dir = Vector(-1, -1)
    },
    DOWN_LEFT {
        override val dir = Vector(-1, -1)
    };
    public abstract val dir: Vector
}