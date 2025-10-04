package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator implements PieceMovesCalculator {

    private final Collection<ChessMove> pawnMoves = new ArrayList<>();

    public void addMove(ChessMove move) {
        pawnMoves.add(move);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

        ChessPiece myPiece = board.getPiece(myPosition);
        ChessGame.TeamColor myTeamColor = myPiece.getTeamColor();

        if (myTeamColor == ChessGame.TeamColor.WHITE) {
            pawnMovesBase(board, myPosition, ChessGame.TeamColor.WHITE);
            pawnMovesCapture(board, myPosition, ChessGame.TeamColor.WHITE);
        } else {
            pawnMovesBase(board, myPosition, ChessGame.TeamColor.BLACK);
            pawnMovesCapture(board, myPosition, ChessGame.TeamColor.BLACK);
        }

        return pawnMoves;

    }

    private void pawnMovesBase(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor teamColor) {

        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();
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
                if (currRow == promotionRow) {
                    addMove(new ChessMove(myPosition, currPosition, ChessPiece.PieceType.QUEEN));
                    addMove(new ChessMove(myPosition, currPosition, ChessPiece.PieceType.ROOK));
                    addMove(new ChessMove(myPosition, currPosition, ChessPiece.PieceType.BISHOP));
                    addMove(new ChessMove(myPosition, currPosition, ChessPiece.PieceType.KNIGHT));
                } else {
                    addMove(new ChessMove(myPosition, currPosition, null));
                }

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
                        addMove(new ChessMove(myPosition, currPosition, null));
                    }
                }
            }
        }
    }

    private void pawnMovesCapture(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor teamColor) {
        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();
        boolean isWhite = teamColor != ChessGame.TeamColor.BLACK;

        // Capture Left
        int currRow = isWhite ? startRow+1 : startRow-1;
        int currCol = startCol-1;
        int promotionRow = isWhite ? 8 : 1;

        // Still on the board
        if (currCol >= 1 && currRow <= 8 && currRow >= 1) {
            ChessPosition currPosition = new ChessPosition(currRow, currCol);
            ChessPiece currPiece = board.getPiece(currPosition);
            if (currPiece != null && currPiece.getTeamColor() != teamColor) {
                if (currRow == promotionRow) {
                    addMove(new ChessMove(myPosition, currPosition, ChessPiece.PieceType.QUEEN));
                    addMove(new ChessMove(myPosition, currPosition, ChessPiece.PieceType.ROOK));
                    addMove(new ChessMove(myPosition, currPosition, ChessPiece.PieceType.BISHOP));
                    addMove(new ChessMove(myPosition, currPosition, ChessPiece.PieceType.KNIGHT));
                } else {
                    addMove(new ChessMove(myPosition, currPosition, null));
                }
            }
        }

        // Capture Right
        currRow = isWhite ? startRow+1 : startRow-1;
        currCol = startCol+1;

        // Still on the board
        if (currCol <= 8 && currRow <= 8 && currRow >= 1) {
            ChessPosition currPosition = new ChessPosition(currRow, currCol);
            ChessPiece currPiece = board.getPiece(currPosition);
            if (currPiece != null && currPiece.getTeamColor() != teamColor) {
                if (currRow == promotionRow) {
                    addMove(new ChessMove(myPosition, currPosition, ChessPiece.PieceType.QUEEN));
                    addMove(new ChessMove(myPosition, currPosition, ChessPiece.PieceType.ROOK));
                    addMove(new ChessMove(myPosition, currPosition, ChessPiece.PieceType.BISHOP));
                    addMove(new ChessMove(myPosition, currPosition, ChessPiece.PieceType.KNIGHT));
                } else {
                    addMove(new ChessMove(myPosition, currPosition, null));
                }
            }
        }
    }
}
