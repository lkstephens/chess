package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BishopMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessPiece myPiece = board.getPiece(myPosition);
        ChessGame.TeamColor myTeamColor = myPiece.getTeamColor();

        Collection<ChessMove> bishopMoves = new ArrayList<>();

        // Up-Right
        while (row < 8 && col < 8) {
            row++;
            col++;
            ChessPosition curr_pos = new ChessPosition(row, col);
            ChessPiece curr_piece = board.getPiece(curr_pos);

            // Position is not occupied by a piece (ASK TA about this warning)
            if (curr_piece == null) {

                bishopMoves.add(new ChessMove(myPosition, curr_pos, null));

            // Position is occupied
            } else {
                ChessGame.TeamColor curr_team_color = curr_piece.getTeamColor();
                // Capture
                if (myTeamColor != curr_team_color) {
                    bishopMoves.add(new ChessMove(myPosition, curr_pos, null));
                }
                break;
            }
        }

        row = myPosition.getRow();
        col = myPosition.getColumn();

        // Down-Right
        while (row > 1 && col < 8) {
            row--;
            col++;
            ChessPosition curr_pos = new ChessPosition(row, col);
            ChessPiece curr_piece = board.getPiece(curr_pos);

            // Position is not occupied by a piece (ASK TA about this warning)
            if (curr_piece == null) {

                bishopMoves.add(new ChessMove(myPosition, curr_pos, null));

                // Position is occupied
            } else {
                ChessGame.TeamColor curr_team_color = curr_piece.getTeamColor();
                // Capture
                if (myTeamColor != curr_team_color) {
                    bishopMoves.add(new ChessMove(myPosition, curr_pos, null));
                }
                break;
            }
        }

        row = myPosition.getRow();
        col = myPosition.getColumn();

        // Down-Right
        while (row > 1 && col > 1) {
            row--;
            col--;
            ChessPosition curr_pos = new ChessPosition(row, col);
            ChessPiece curr_piece = board.getPiece(curr_pos);

            // Position is not occupied by a piece (ASK TA about this warning)
            if (curr_piece == null) {

                bishopMoves.add(new ChessMove(myPosition, curr_pos, null));

                // Position is occupied
            } else {
                ChessGame.TeamColor curr_team_color = curr_piece.getTeamColor();
                // Capture
                if (myTeamColor != curr_team_color) {
                    bishopMoves.add(new ChessMove(myPosition, curr_pos, null));
                }
                break;
            }
        }

        row = myPosition.getRow();
        col = myPosition.getColumn();

        // Down-Right
        while (row < 8 && col > 1) {
            row++;
            col--;
            ChessPosition curr_pos = new ChessPosition(row, col);
            ChessPiece curr_piece = board.getPiece(curr_pos);

            // Position is not occupied by a piece (ASK TA about this warning)
            if (curr_piece == null) {

                bishopMoves.add(new ChessMove(myPosition, curr_pos, null));

                // Position is occupied
            } else {
                ChessGame.TeamColor curr_team_color = curr_piece.getTeamColor();
                // Capture
                if (myTeamColor != curr_team_color) {
                    bishopMoves.add(new ChessMove(myPosition, curr_pos, null));
                }
                break;
            }
        }

        return bishopMoves;
    }
}
