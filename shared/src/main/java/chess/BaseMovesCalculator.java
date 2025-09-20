package chess;

import java.util.ArrayList;
import java.util.Collection;

public abstract class BaseMovesCalculator implements PieceMovesCalculator{

    private Collection<ChessMove> moves = new ArrayList<>();
    private int[][] directions;
    private boolean distIteration = false;

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

            int x = direction[0];
            int y = direction[1];
            int currRow = startRow;
            int currCol = startCol;

            while ((currRow+y) != 0 && (currRow+y) != 9 && (currCol+x) != 0 && (currCol+x) != 9) {
                currRow += y;
                currCol += x;
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
