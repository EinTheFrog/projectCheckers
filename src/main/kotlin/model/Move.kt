package model

enum class Move(val vector: Vector, val direction: Direction, val isAttack: Boolean) {
    GO_UP_LEFT (Vector(-1, -1), Direction.UP, false),
    GO_UP_RIGHT (Vector(1, -1), Direction.UP, false),
    GO_DOWN_RIGHT(Vector(1, 1), Direction.DOWN, false),
    GO_DOWN_LEFT (Vector(-1, 1), Direction.DOWN, false),

    ATTACK_UP_LEFT (Vector(-2, -2), Direction.UP, true),
    ATTACK_UP_RIGHT (Vector(2, -2), Direction.UP, true),
    ATTACK_DOWN_RIGHT(Vector(2, 2), Direction.DOWN, true),
    ATTACK_DOWN_LEFT (Vector(-2, 2), Direction.DOWN, true),
}