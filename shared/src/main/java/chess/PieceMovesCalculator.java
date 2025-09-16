package chess;


import java.util.Collection;

/**
 * Helper interface to calculate piece moves
 */
public interface PieceMovesCalculator {
    // Calculates the possible moves for a certain type of piece
    Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);
}


