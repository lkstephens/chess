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
            case BISHOP -> new BaseMovesCalculator(PieceType.BISHOP);
            case KING -> new BaseMovesCalculator(PieceType.KING);
            case KNIGHT -> new BaseMovesCalculator(PieceType.KNIGHT);
            case PAWN -> new PawnMovesCalculator();
            case QUEEN -> new BaseMovesCalculator(PieceType.QUEEN);
            case ROOK -> new BaseMovesCalculator(PieceType.ROOK);
        };

        return movesCalculator.pieceMoves(board, myPosition);
    }

    @Override
    public String toString() {
        if (pieceColor == ChessGame.TeamColor.WHITE) {
            return switch (type) {
                case KING -> "K";
                case QUEEN -> "Q";
                case ROOK -> "R";
                case BISHOP -> "B";
                case KNIGHT -> "N";
                case PAWN -> "P";
            };
        } else {
            return switch (type) {
                case KING -> "k";
                case QUEEN -> "q";
                case ROOK -> "r";
                case BISHOP -> "b";
                case KNIGHT -> "n";
                case PAWN -> "p";
            };
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
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
