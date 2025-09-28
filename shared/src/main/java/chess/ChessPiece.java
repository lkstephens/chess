package chess;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece implements Cloneable{

    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

        ChessPiece currPiece = board.getPiece(myPosition);

        PieceMovesCalculator movesCalculator = switch (currPiece.getPieceType()) {
            case PieceType.BISHOP -> new BishopMovesCalculator(new int[][]{{1, 1}, {1, -1}, {-1, -1}, {-1, 1}}, true);
            case PieceType.KING -> new KingMovesCalculator(new int[][]{{1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}, {-1, 0}, {-1, 1}, {0, 1}}, false);
            case PieceType.KNIGHT -> new KnightMovesCalculator(new int[][]{{1, 2}, {2, 1}, {2, -1}, {1, -2}, {-1, -2}, {-2, -1}, {-2, 1}, {-1, 2}}, false);
            case PieceType.PAWN -> new PawnMovesCalculator();
            case PieceType.QUEEN -> new QueenMovesCalculator(new int[][]{{1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}, {-1, 0}, {-1, 1}, {0, 1}}, true);
            case PieceType.ROOK -> new RookMovesCalculator(new int[][]{{0, 1}, {1, 0}, {0, -1}, {-1, 0}}, true);
        };

        return movesCalculator.pieceMoves(board, myPosition);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        // May need to edit return statement to use ==
        return pieceColor.equals(that.pieceColor) && type.equals(that.type);
    }

    @Override
    public int hashCode() {
        return 71 * Objects.hash(pieceColor, type);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
