package model

class Vector(val x: Int, val y: Int): Cloneable {
    operator fun plus(another: Vector): Vector = Vector( x + another.x,  y + another.y)
    operator fun minus(another: Vector): Vector = Vector(x - another.x, y - another.y)
    operator fun times(factor: Int) = Vector(x * factor, y * factor)
    operator fun div(factor: Int) = Vector(x / factor, y / factor)

    override fun equals(other: Any?): Boolean {
        if (other !is Vector) return false
        return other.x == x && other.y == y
    }

    override fun hashCode(): Int {
        return x + 31 * y
    }

    override fun toString(): String {
        return "{Vector{x: $x, y: $y}}"
    }

    public override fun clone(): Vector {
        return Vector(x, y)
    }

}