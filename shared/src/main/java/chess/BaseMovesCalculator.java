package chess;

import java.util.ArrayList;
import java.util.Collection;

public abstract class BaseMovesCalculator implements PieceMovesCalculator{

    private final Collection<ChessMove> moves = new ArrayList<>();
    private final int[][] directions;
    private final boolean distIteration;

    public BaseMovesCalculator(int[][] directions, boolean distIteration) {
        this.directions = directions;
        this.distIteration = distIteration;
    }

    private void addMove(ChessMove move) {
        moves.add(move);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

        int startRow = myPosition.getRow();
        int startCol = myPosition.getColumn();
        ChessPiece myPiece = board.getPiece(myPosition);
        ChessGame.TeamColor myTeamColor = myPiece.getTeamColor();

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
