package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BaseMovesCalculator implements PieceMovesCalculator{

    private final Collection<ChessMove> moves = new ArrayList<>();
    private final ChessPiece.PieceType pieceType;

    public BaseMovesCalculator(ChessPiece.PieceType pieceType) {
        this.pieceType = pieceType;
    }

    private void addMove(ChessMove move) {
        moves.add(move);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

        int[][] directions = switch (pieceType) {
            case BISHOP -> new int[][]{{1, 1}, {1, -1}, {-1, -1}, {-1, 1}};
            case KING, QUEEN -> new int[][]{{1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}, {-1, 0}, {-1, 1}, {0, 1}};
            case KNIGHT -> new int[][]{{1, 2}, {2, 1}, {2, -1}, {1, -2}, {-1, -2}, {-2, -1}, {-2, 1}, {-1, 2}};
            case ROOK -> new int[][]{{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
            case PAWN -> null;
        };

        boolean distIteration;
        distIteration = pieceType != ChessPiece.PieceType.KING && pieceType != ChessPiece.PieceType.KNIGHT;

        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();
        ChessPiece myPiece = board.getPiece(myPosition);
        ChessGame.TeamColor myTeamColor = myPiece.getTeamColor();

        assert directions != null;
        for (int[] direction : directions) {
            if (direction.length != 2) {
                System.out.println("Direction array needs 2 values");
            }

            int over = direction[0];
            int up = direction[1];
            int currRow = startRow;
            int currCol = startCol;

            while ((currRow+up) >= 1 && (currRow+up) <= 8 && (currCol+over) >= 1 && (currCol+over) <= 8) {
                currRow += up;
                currCol += over;
                ChessPosition currPosition = new ChessPosition(currRow, currCol);
                ChessPiece currPiece = board.getPiece(currPosition);

                if (currPiece == null) {
                    addMove(new ChessMove(myPosition, currPosition, null));

                } else {
                    ChessGame.TeamColor currTeamColor = currPiece.getTeamColor();
                    // Capture
                    if (myTeamColor != currTeamColor) {
                        addMove(new ChessMove(myPosition, currPosition, null));
                    }
                    break;
                }

                // If it's a King/Knight/Pawn iterate once
                if (!distIteration) {
                    break;
                }
            }
        }

        return moves;
    }



}
