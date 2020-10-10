package model

/**
 * Хранит фигуру и список ее действий (список, т.к. фигура может сделать несколько атак)
 */
data class Turn (
        val piece: Piece,
        val moves: List<Move>
)