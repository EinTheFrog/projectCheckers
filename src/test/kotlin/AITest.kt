import model.*
import org.junit.Test

class AITest {
    @Test
    fun makeTurn() {
        val ai = AI()

        val board = Board(0)
        val blackChecker = Piece(1, PieceType.CHECKER, Vector(1, 0), 0, Direction.DOWN)
        val whiteChecker = Piece(2, PieceType.CHECKER, Vector(2, 1), 1, Direction.UP)
        board[1, 0].piece = blackChecker
        board[2, 1].piece = whiteChecker

        val turn = ai.makeTurn(board, 1)
        assert(turn == Turn(blackChecker, listOf(Move.ATTACK_DOWN_RIGHT)))
    }
}