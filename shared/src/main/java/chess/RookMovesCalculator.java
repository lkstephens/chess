package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessPiece myPiece = board.getPiece(myPosition);
        ChessGame.TeamColor myTeamColor = myPiece.getTeamColor();
        Collection<ChessMove> rookMoves = new ArrayList<>();

        // Right
        while (col < 8) {
            col++;
            ChessPosition curr_pos = new ChessPosition(row, col);
            ChessPiece curr_piece = board.getPiece(curr_pos);

            // Position is not occupied by a piece
            if (curr_piece == null) {

                rookMoves.add(new ChessMove(myPosition, curr_pos, null));

                // Position is occupied
            } else {
                ChessGame.TeamColor curr_team_color = curr_piece.getTeamColor();
                // Capture
                if (myTeamColor != curr_team_color) {
                    rookMoves.add(new ChessMove(myPosition, curr_pos, null));
                }
                break;
            }
        }

        row = myPosition.getRow();
        col = myPosition.getColumn();

        // Down
        while (row > 1) {
            row--;
            ChessPosition curr_pos = new ChessPosition(row, col);
            ChessPiece curr_piece = board.getPiece(curr_pos);

            // Position is not occupied by a piece
            if (curr_piece == null) {

                rookMoves.add(new ChessMove(myPosition, curr_pos, null));

                // Position is occupied
            } else {
                ChessGame.TeamColor curr_team_color = curr_piece.getTeamColor();
                // Capture
                if (myTeamColor != curr_team_color) {
                    rookMoves.add(new ChessMove(myPosition, curr_pos, null));
                }
                break;
            }
        }

        row = myPosition.getRow();
        col = myPosition.getColumn();

        // Left
        while (col > 1) {
            col--;
            ChessPosition curr_pos = new ChessPosition(row, col);
            ChessPiece curr_piece = board.getPiece(curr_pos);

            // Position is not occupied by a piece
            if (curr_piece == null) {

                rookMoves.add(new ChessMove(myPosition, curr_pos, null));

                // Position is occupied
            } else {
                ChessGame.TeamColor curr_team_color = curr_piece.getTeamColor();
                // Capture
                if (myTeamColor != curr_team_color) {
                    rookMoves.add(new ChessMove(myPosition, curr_pos, null));
                }
                break;
            }
        }

        row = myPosition.getRow();
        col = myPosition.getColumn();

        // Up
        while (row < 8) {
            row++;
            ChessPosition curr_pos = new ChessPosition(row, col);
            ChessPiece curr_piece = board.getPiece(curr_pos);

            // Position is not occupied by a piece
            if (curr_piece == null) {

                rookMoves.add(new ChessMove(myPosition, curr_pos, null));

                // Position is occupied
            } else {
                ChessGame.TeamColor curr_team_color = curr_piece.getTeamColor();
                // Capture
                if (myTeamColor != curr_team_color) {
                    rookMoves.add(new ChessMove(myPosition, curr_pos, null));
                }
                break;
            }
        }

        return rookMoves;

    }
}
