package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator implements PieceMovesCalculator {

    private final Collection<ChessMove> pawnMoves = new ArrayList<>();

    public void addMove(ChessMove move) {
        pawnMoves.add(move);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition startPosition) {

        ChessPiece piece = board.getPiece(startPosition);
        ChessGame.TeamColor teamColor = piece.getTeamColor();

        if (teamColor == ChessGame.TeamColor.WHITE) {
            pawnMovesBase(board, startPosition, ChessGame.TeamColor.WHITE);
            pawnMovesCapture(board, startPosition, ChessGame.TeamColor.WHITE);
        } else {
            pawnMovesBase(board, startPosition, ChessGame.TeamColor.BLACK);
            pawnMovesCapture(board, startPosition, ChessGame.TeamColor.BLACK);
        }

        return pawnMoves;

    }

    private void pawnMovesBase(ChessBoard board, ChessPosition startPosition, ChessGame.TeamColor teamColor) {

        int startRow = startPosition.getRow();
        int startCol = startPosition.getColumn();
        boolean isWhite = teamColor != ChessGame.TeamColor.BLACK;

        // Move up 1
        int currRow = isWhite ? startRow+1 : startRow-1;
        int promotionRow = isWhite ? 8 : 1;
        int pawnStartingRow = isWhite ? 2 : 7;

        // Still on the board
        if (currRow >= 1 && currRow <= 8) {

            ChessPosition currPosition = new ChessPosition(currRow, startCol);
            ChessPiece currPiece = board.getPiece(currPosition);

            if (currPiece == null) {
                // Check for promotion
                checkForPromotion(currRow, promotionRow, startPosition, currPosition);

                // Check for double advance
                if (startRow == pawnStartingRow) {
                    if (isWhite) {
                        currRow++;
                    } else {
                        currRow--;
                    }
                    currPosition = new ChessPosition(currRow, startCol);
                    currPiece = board.getPiece(currPosition);

                    if (currPiece == null) {
                        addMove(new ChessMove(startPosition, currPosition, null));
                    }
                }
            }
        }
    }

    private void pawnMovesCapture(ChessBoard board, ChessPosition startPosition, ChessGame.TeamColor teamColor) {
        int startRow = startPosition.getRow();
        int startCol = startPosition.getColumn();
        boolean isWhite = teamColor != ChessGame.TeamColor.BLACK;

        // Capture Left
        int currRow = isWhite ? startRow+1 : startRow-1;
        int currCol = startCol-1;
        int promotionRow = isWhite ? 8 : 1;

        addCaptureMoves(currRow, currCol, board, teamColor, promotionRow, startPosition);

        // Capture Right
        currRow = isWhite ? startRow+1 : startRow-1;
        currCol = startCol+1;

        addCaptureMoves(currRow, currCol, board, teamColor, promotionRow, startPosition);
    }

    public void addCaptureMoves(int currRow, int currCol, ChessBoard board, ChessGame.TeamColor teamColor,
                                int promotionRow, ChessPosition startPosition) {
        // Still on the board
        if (currRow >= 1 && currRow <= 8 && currCol >= 1 && currCol <= 8) {
            ChessPosition currPosition = new ChessPosition(currRow, currCol);
            ChessPiece currPiece = board.getPiece(currPosition);
            if (currPiece != null && currPiece.getTeamColor() != teamColor) {
                checkForPromotion(currRow, promotionRow, startPosition, currPosition);
            }
        }
    }

    public void checkForPromotion(int currRow, int promotionRow,
                                  ChessPosition startPosition, ChessPosition currPosition) {
        if (currRow == promotionRow) {
            addMove(new ChessMove(startPosition, currPosition, ChessPiece.PieceType.QUEEN));
            addMove(new ChessMove(startPosition, currPosition, ChessPiece.PieceType.ROOK));
            addMove(new ChessMove(startPosition, currPosition, ChessPiece.PieceType.BISHOP));
            addMove(new ChessMove(startPosition, currPosition, ChessPiece.PieceType.KNIGHT));
        } else {
            addMove(new ChessMove(startPosition, currPosition, null));
        }
    }
}
