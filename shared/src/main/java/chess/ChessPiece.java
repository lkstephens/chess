package chess;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

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

        // Bishop Move Calculation
        if (currPiece.getPieceType() == PieceType.BISHOP) {
            PieceMovesCalculator bishopMovesCalc = new BishopMovesCalculator(new int[][]{{1,1},{1,-1},{-1,-1},{-1,1}}, true);
            Collection<ChessMove> bishopMoves = bishopMovesCalc.pieceMoves(board, myPosition);
            return bishopMoves;

        // King Move Calculation
        } else if (currPiece.getPieceType() == PieceType.KING){
            PieceMovesCalculator kingMovesCalc = new KingMovesCalculator();
            Collection<ChessMove> kingMoves = kingMovesCalc.pieceMoves(board, myPosition);
            return kingMoves;

        // Knight Move Calculation
        } else if (currPiece.getPieceType() == PieceType.KNIGHT){
            PieceMovesCalculator knightMovesCalc = new KnightMovesCalculator();
            Collection<ChessMove> knightMoves = knightMovesCalc.pieceMoves(board, myPosition);
            return knightMoves;

        // Pawn Move Calculation
        } else if (currPiece.getPieceType() == PieceType.PAWN){
            PieceMovesCalculator pawnMovesCalc = new PawnMovesCalculator();
            Collection<ChessMove> pawnMoves = pawnMovesCalc.pieceMoves(board, myPosition);
            return pawnMoves;

        // Queen Move Calculation
        } else if (currPiece.getPieceType() == PieceType.QUEEN){
            PieceMovesCalculator queenMovesCalc = new QueenMovesCalculator();
            Collection<ChessMove> queenMoves = queenMovesCalc.pieceMoves(board, myPosition);
            return queenMoves;

        // Rook Move Calculation
        } else if (currPiece.getPieceType() == PieceType.ROOK){
            PieceMovesCalculator rookMovesCalc = new RookMovesCalculator();
            Collection<ChessMove> rookMoves = rookMovesCalc.pieceMoves(board, myPosition);
            return rookMoves;

        } else {
            return List.of();
        }
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
}
