package model

enum class Move {
    UP_LEFT {
        override val x = -1
        override val y = 1
    },
    UP_RIGHT {
        override val x = 1
        override val y = 1
    },
    DOWN_RIGHT{
        override val x = 1
        override val y = -1
    },
    DOWN_LEFT {
        override val x = -1
        override val y = -1
    };
    public abstract val x: Int
    public abstract val y: Int
}