import javafx.geometry.Pos
import model.*
import org.junit.Test

class BoardTest {
    @Test
    fun testCanPieceMakeThisMove() {
        val board = Board(0)
        val blackChecker = Piece(PieceType.CHECKER, Vector(1, 0), 0, Direction.DOWN)
        board[1, 0].piece = blackChecker
        assert(!board.canPieceMakeThisMove(blackChecker, Move.ATTACK_DOWN_LEFT))
        assert(!board.canPieceMakeThisMove(blackChecker, Move.GO_UP_LEFT))
        assert(!board.canPieceMakeThisMove(blackChecker, Move.ATTACK_UP_RIGHT))
        assert(board.canPieceMakeThisMove(blackChecker, Move.GO_DOWN_RIGHT))
    }

    @Test
    fun testGetAvailableTurns() {
        val board = Board(0)
        val blackChecker = Piece(PieceType.CHECKER, Vector(1, 0), 0, Direction.DOWN)
        val whiteChecker = Piece(PieceType.CHECKER, Vector(7, 0), 1, Direction.UP)
        board[1, 0].piece = blackChecker
        board[7, 0].piece = whiteChecker
        assert(board.getAvailableTurns().size == 1)
        assert(board.getAvailableTurns()[blackChecker]?.size == 2)
    }

    @Test
    fun testMovePiece() {
        val board = Board(0)
        val blackChecker = Piece(PieceType.CHECKER, Vector(1, 0), 0, Direction.DOWN)
        val whiteChecker = Piece(PieceType.CHECKER, Vector(0, 7), 1, Direction.UP)
        board[1, 0].piece = blackChecker
        board[0, 7].piece = whiteChecker
        board.makeTurn(Turn(blackChecker, listOf(Move.GO_DOWN_LEFT)))

        assert(board[0, 0].piece == null)
        assert(board[0, 1].piece == blackChecker)
        assert(board.getAvailableTurns().size == 1)
        assert(board.getAvailableTurns()[whiteChecker]?.size == 1)
    }

    @Test
    fun testAttackWithPiece() {
        val board = Board(0)
        val blackChecker = Piece(PieceType.CHECKER, Vector(1, 0), 0, Direction.DOWN)
        val whiteChecker = Piece(PieceType.CHECKER, Vector(2, 1), 1, Direction.UP)
        board[1, 0].piece = blackChecker
        board[2, 1].piece = whiteChecker
        board.makeTurn(Turn(blackChecker, listOf(Move.ATTACK_DOWN_RIGHT)))

        assert(board[0, 0].piece == null)
        assert(board[3, 2].piece == blackChecker)
        assert(board[2, 1].piece == null)

        board[3, 2].piece = null

        blackChecker.pos = Vector(1, 0)
        board[1, 0].piece = blackChecker

        val whiteChecker1 = Piece(PieceType.CHECKER, Vector(2, 1), 1, Direction.UP)
        val whiteChecker2 = Piece(PieceType.CHECKER, Vector(4, 3), 1, Direction.UP)
        val whiteChecker3 = Piece(PieceType.CHECKER, Vector(6, 3), 1, Direction.UP)
        board[2, 1] = whiteChecker1
        board[4, 3] = whiteChecker2
        board[6, 3] = whiteChecker3

        val moves = board.getAvailableMovesForPiece(blackChecker)
        board.makeTurn(Turn(blackChecker, moves.first()))

        assert(board[2, 1].piece == null)
        assert(board[4, 3].piece == null)
        assert(board[6, 3].piece == whiteChecker3)
        assert(board[5, 4].piece == blackChecker )
    }
}