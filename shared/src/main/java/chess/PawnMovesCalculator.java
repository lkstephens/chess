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
            whitePawnMovesBase(board, myPosition);
            whitePawnMovesCapture(board, myPosition);
        } else {
            blackPawnMovesBase(board, myPosition);
            blackPawnMovesCapture(board, myPosition);
        }

        return pawnMoves;

    }

    private void whitePawnMovesBase(ChessBoard board, ChessPosition myPosition) {

        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();

        // Move up 1
        int currRow = startRow+1;

        // Still on the board
        if (currRow <= 8) {

            ChessPosition currPosition = new ChessPosition(currRow, startCol);
            ChessPiece currPiece = board.getPiece(currPosition);

            if (currPiece == null) {
                // Check for promotion
                if (currRow == 8) {
                    addMove(new ChessMove(myPosition, currPosition, ChessPiece.PieceType.QUEEN));
                    addMove(new ChessMove(myPosition, currPosition, ChessPiece.PieceType.ROOK));
                    addMove(new ChessMove(myPosition, currPosition, ChessPiece.PieceType.BISHOP));
                    addMove(new ChessMove(myPosition, currPosition, ChessPiece.PieceType.KNIGHT));
                } else {
                    addMove(new ChessMove(myPosition, currPosition, null));
                }

                // Check for double advance
                if (startRow == 2) {
                    currRow++;
                    currPosition = new ChessPosition(currRow, startCol);
                    currPiece = board.getPiece(currPosition);

                    if (currPiece == null) {
                        addMove(new ChessMove(myPosition, currPosition, null));
                    }
                }
            }
        }
    }

    private void blackPawnMovesBase(ChessBoard board, ChessPosition myPosition) {

        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();

        // Move down 1
        int currRow = startRow-1;

        // Still on the board
        if (currRow >= 1) {

            ChessPosition currPosition = new ChessPosition(currRow, startCol);
            ChessPiece currPiece = board.getPiece(currPosition);

            if (currPiece == null) {
                // Check for Promotion
                if (currRow == 1) {
                    addMove(new ChessMove(myPosition, currPosition, ChessPiece.PieceType.QUEEN));
                    addMove(new ChessMove(myPosition, currPosition, ChessPiece.PieceType.ROOK));
                    addMove(new ChessMove(myPosition, currPosition, ChessPiece.PieceType.BISHOP));
                    addMove(new ChessMove(myPosition, currPosition, ChessPiece.PieceType.KNIGHT));
                } else {
                    addMove(new ChessMove(myPosition, currPosition, null));
                }

                // Check for double advance
                if (startRow == 7) {
                    currRow--;
                    currPosition = new ChessPosition(currRow, startCol);
                    currPiece = board.getPiece(currPosition);

                    if (currPiece == null) {
                        addMove(new ChessMove(myPosition, currPosition, null));
                    }
                }
            }
        }
    }

    private void whitePawnMovesCapture(ChessBoard board, ChessPosition myPosition) {
        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();

        // Capture Left
        int currRow = startRow+1;
        int currCol = startCol-1;

        // Still on the board
        if (currCol >= 1 && currRow <= 8) {
            ChessPosition currPosition = new ChessPosition(currRow, currCol);
            ChessPiece currPiece = board.getPiece(currPosition);
            if (currPiece != null && currPiece.getTeamColor() == ChessGame.TeamColor.BLACK) {
                if (currRow == 8) {
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
        currRow = startRow+1;
        currCol = startCol+1;

        // Still on the board
        if (currCol <= 8 && currRow <= 8) {
            ChessPosition currPosition = new ChessPosition(currRow, currCol);
            ChessPiece currPiece = board.getPiece(currPosition);
            if (currPiece != null && currPiece.getTeamColor() == ChessGame.TeamColor.BLACK) {
                if (currRow == 8) {
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

    private void blackPawnMovesCapture(ChessBoard board, ChessPosition myPosition) {
        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();

        // Capture Left
        int currRow = startRow-1;
        int currCol = startCol-1;

        // Still on the board
        if (currCol >= 1 && currRow >= 1) {
            ChessPosition currPosition = new ChessPosition(currRow, currCol);
            ChessPiece currPiece = board.getPiece(currPosition);
            if (currPiece != null && currPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                if (currRow == 1) {
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
        currRow = startRow-1;
        currCol = startCol+1;

        // Still on the board
        if (currCol <= 8 && currRow >= 1) {
            ChessPosition currPosition = new ChessPosition(currRow, currCol);
            ChessPiece currPiece = board.getPiece(currPosition);
            if (currPiece != null && currPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                if (currRow == 1) {
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
