package chess;


import java.util.Collection;

/**
 * Helper interface to calculate piece moves
 */
public interface PieceMovesCalculator {
    // Calculation of Moves
    Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);
}


