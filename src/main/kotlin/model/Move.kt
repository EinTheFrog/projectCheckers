package model

enum class Move {
    GO_UP_LEFT {
        override val vector = Vector(-1, -1)
        override val isAttack = false
        override val direction = Direction.UP
    },
    GO_UP_RIGHT {
        override val vector = Vector(1, -1)
        override val isAttack = false
        override val direction = Direction.UP
    },
    GO_DOWN_RIGHT{
        override val vector = Vector(1, 1)
        override val isAttack = false
        override val direction = Direction.DOWN
    },
    GO_DOWN_LEFT {
        override val vector = Vector(-1, 1)
        override val isAttack = false
        override val direction = Direction.DOWN
    },
    ATTACK_UP_LEFT {
        override val vector = Vector(-2, -2)
        override val isAttack = true
        override val direction = Direction.UP
    },
    ATTACK_UP_RIGHT {
        override val vector = Vector(2, -2)
        override val isAttack = true
        override val direction = Direction.UP
    },
    ATTACK_DOWN_RIGHT{
        override val vector = Vector(2, 2)
        override val isAttack = true
        override val direction = Direction.DOWN
    },
    ATTACK_DOWN_LEFT {
        override val vector = Vector(-2, 2)
        override val isAttack = true
        override val direction = Direction.DOWN
    };
    public abstract val direction: Direction
    public abstract val vector: Vector
    public abstract val isAttack: Boolean
}