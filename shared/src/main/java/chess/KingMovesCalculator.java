package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessPiece myPiece = board.getPiece(myPosition);
        ChessGame.TeamColor myTeamColor = myPiece.getTeamColor();
        Collection<ChessMove> kingMoves = new ArrayList<>();

        // Up-Right
        row++;
        col++;
        if (row < 9 && col < 9) {
            ChessPosition curr_pos = new ChessPosition(row, col);
            ChessPiece curr_piece = board.getPiece(curr_pos);

            // Position is not occupied by a piece
            if (curr_piece == null) {

                kingMoves.add(new ChessMove(myPosition, curr_pos, null));

                // Position is occupied
            } else {
                ChessGame.TeamColor curr_team_color = curr_piece.getTeamColor();
                // Capture
                if (myTeamColor != curr_team_color) {
                    kingMoves.add(new ChessMove(myPosition, curr_pos, null));
                }
            }
        }

        row = myPosition.getRow();
        col = myPosition.getColumn();

        // Right
        col++;
        if (col < 9) {
            ChessPosition curr_pos = new ChessPosition(row, col);
            ChessPiece curr_piece = board.getPiece(curr_pos);

            // Position is not occupied by a piece
            if (curr_piece == null) {

                kingMoves.add(new ChessMove(myPosition, curr_pos, null));

                // Position is occupied
            } else {
                ChessGame.TeamColor curr_team_color = curr_piece.getTeamColor();
                // Capture
                if (myTeamColor != curr_team_color) {
                    kingMoves.add(new ChessMove(myPosition, curr_pos, null));
                }
            }
        }

        row = myPosition.getRow();
        col = myPosition.getColumn();

        // Down-Right
        row--;
        col++;
        if (row > 0 && col < 9) {
            ChessPosition curr_pos = new ChessPosition(row, col);
            ChessPiece curr_piece = board.getPiece(curr_pos);

            // Position is not occupied by a piece
            if (curr_piece == null) {

                kingMoves.add(new ChessMove(myPosition, curr_pos, null));

                // Position is occupied
            } else {
                ChessGame.TeamColor curr_team_color = curr_piece.getTeamColor();
                // Capture
                if (myTeamColor != curr_team_color) {
                    kingMoves.add(new ChessMove(myPosition, curr_pos, null));
                }
            }
        }

        row = myPosition.getRow();
        col = myPosition.getColumn();

        // Down
        row--;
        if (row > 0) {
            ChessPosition curr_pos = new ChessPosition(row, col);
            ChessPiece curr_piece = board.getPiece(curr_pos);

            // Position is not occupied by a piece
            if (curr_piece == null) {

                kingMoves.add(new ChessMove(myPosition, curr_pos, null));

                // Position is occupied
            } else {
                ChessGame.TeamColor curr_team_color = curr_piece.getTeamColor();
                // Capture
                if (myTeamColor != curr_team_color) {
                    kingMoves.add(new ChessMove(myPosition, curr_pos, null));
                }
            }
        }

        row = myPosition.getRow();
        col = myPosition.getColumn();

        // Down-Left
        row--;
        col--;
        if (row > 0 && col > 0) {
            ChessPosition curr_pos = new ChessPosition(row, col);
            ChessPiece curr_piece = board.getPiece(curr_pos);

            // Position is not occupied by a piece
            if (curr_piece == null) {

                kingMoves.add(new ChessMove(myPosition, curr_pos, null));

                // Position is occupied
            } else {
                ChessGame.TeamColor curr_team_color = curr_piece.getTeamColor();
                // Capture
                if (myTeamColor != curr_team_color) {
                    kingMoves.add(new ChessMove(myPosition, curr_pos, null));
                }
            }
        }

        row = myPosition.getRow();
        col = myPosition.getColumn();

        // Left
        col--;
        if (col > 0) {
            ChessPosition curr_pos = new ChessPosition(row, col);
            ChessPiece curr_piece = board.getPiece(curr_pos);

            // Position is not occupied by a piece
            if (curr_piece == null) {

                kingMoves.add(new ChessMove(myPosition, curr_pos, null));

                // Position is occupied
            } else {
                ChessGame.TeamColor curr_team_color = curr_piece.getTeamColor();
                // Capture
                if (myTeamColor != curr_team_color) {
                    kingMoves.add(new ChessMove(myPosition, curr_pos, null));
                }
            }
        }


        row = myPosition.getRow();
        col = myPosition.getColumn();

        // Up-Left
        row++;
        col--;
        if (row < 9 && col > 0) {
            ChessPosition curr_pos = new ChessPosition(row, col);
            ChessPiece curr_piece = board.getPiece(curr_pos);

            // Position is not occupied by a piece
            if (curr_piece == null) {

                kingMoves.add(new ChessMove(myPosition, curr_pos, null));

                // Position is occupied
            } else {
                ChessGame.TeamColor curr_team_color = curr_piece.getTeamColor();
                // Capture
                if (myTeamColor != curr_team_color) {
                    kingMoves.add(new ChessMove(myPosition, curr_pos, null));
                }
            }
        }

        row = myPosition.getRow();
        col = myPosition.getColumn();

        // Up
        row++;
        if (row < 9) {
            ChessPosition curr_pos = new ChessPosition(row, col);
            ChessPiece curr_piece = board.getPiece(curr_pos);

            // Position is not occupied by a piece
            if (curr_piece == null) {

                kingMoves.add(new ChessMove(myPosition, curr_pos, null));

                // Position is occupied
            } else {
                ChessGame.TeamColor curr_team_color = curr_piece.getTeamColor();
                // Capture
                if (myTeamColor != curr_team_color) {
                    kingMoves.add(new ChessMove(myPosition, curr_pos, null));
                }
            }
        }

        return kingMoves;

    }
}
