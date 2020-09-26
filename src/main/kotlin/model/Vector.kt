package model

class Vector(val x: Int, val y: Int) {
    operator fun plus(another: Vector): Vector = Vector( x + another.x,  y + another.y)
    operator fun minus(another: Vector): Vector = Vector(x - another.x, y - another.y)
    operator fun times(factor: Int) = Vector(x * factor, y * factor)

    override fun equals(other: Any?): Boolean {
        if (other !is Vector) return false
        return other.x == x && other.y == y
    }

    override fun hashCode(): Int {
        return x + 31 * y
    }
}