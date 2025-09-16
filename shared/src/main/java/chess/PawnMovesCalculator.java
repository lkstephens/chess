package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessPiece myPiece = board.getPiece(myPosition);
        ChessGame.TeamColor myTeamColor = myPiece.getTeamColor();
        Collection<ChessMove> pawnMoves = new ArrayList<>();

        if (myTeamColor == ChessGame.TeamColor.WHITE) {

            // Move up 1
            ChessPosition curr_pos = new ChessPosition(row+1, col);
            ChessPiece curr_piece = board.getPiece(curr_pos);
            if (curr_piece == null) {

                if (row+1 == 8) {
                    pawnMoves.add(new ChessMove(myPosition, curr_pos, ChessPiece.PieceType.QUEEN));
                } else {
                    pawnMoves.add(new ChessMove(myPosition, curr_pos, null));
                }



            // Position is occupied
            } else {
                // Do nothing
            }

            // Capture Up-Right

            // Capture Up-Left

            // Move up 2
            if (row == 2) {
                curr_pos = new ChessPosition(row+2, col);
                curr_piece = board.getPiece(curr_pos);
                if (curr_piece == null) {

                    pawnMoves.add(new ChessMove(myPosition, curr_pos, null));

                    // Position is occupied
                } else {
                    ChessGame.TeamColor curr_team_color = curr_piece.getTeamColor();
                    // Capture
                    if (myTeamColor != curr_team_color) {
                        pawnMoves.add(new ChessMove(myPosition, curr_pos, null));
                    }
                }

            }
        }

        return pawnMoves;

    }
}
