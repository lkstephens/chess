package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessPiece myPiece = board.getPiece(myPosition);
        ChessGame.TeamColor myTeamColor = myPiece.getTeamColor();
        Collection<ChessMove> knightMoves = new ArrayList<>();

        // Up-Right
        row = row+2;
        col++;
        if (row < 9 && col < 9) {
            ChessPosition curr_pos = new ChessPosition(row, col);
            ChessPiece curr_piece = board.getPiece(curr_pos);

            // Position is not occupied by a piece
            if (curr_piece == null) {

                knightMoves.add(new ChessMove(myPosition, curr_pos, null));

                // Position is occupied
            } else {
                ChessGame.TeamColor curr_team_color = curr_piece.getTeamColor();
                // Capture
                if (myTeamColor != curr_team_color) {
                    knightMoves.add(new ChessMove(myPosition, curr_pos, null));
                }
            }
        }

        row = myPosition.getRow();
        col = myPosition.getColumn();

        // Right-Up
        row++;
        col = col+2;
        if (row < 9 && col < 9) {
            ChessPosition curr_pos = new ChessPosition(row, col);
            ChessPiece curr_piece = board.getPiece(curr_pos);

            // Position is not occupied by a piece
            if (curr_piece == null) {

                knightMoves.add(new ChessMove(myPosition, curr_pos, null));

                // Position is occupied
            } else {
                ChessGame.TeamColor curr_team_color = curr_piece.getTeamColor();
                // Capture
                if (myTeamColor != curr_team_color) {
                    knightMoves.add(new ChessMove(myPosition, curr_pos, null));
                }
            }
        }

        row = myPosition.getRow();
        col = myPosition.getColumn();

        // Right-Down
        row--;
        col = col+2;
        if (row > 0 && col < 9) {
            ChessPosition curr_pos = new ChessPosition(row, col);
            ChessPiece curr_piece = board.getPiece(curr_pos);

            // Position is not occupied by a piece
            if (curr_piece == null) {

                knightMoves.add(new ChessMove(myPosition, curr_pos, null));

                // Position is occupied
            } else {
                ChessGame.TeamColor curr_team_color = curr_piece.getTeamColor();
                // Capture
                if (myTeamColor != curr_team_color) {
                    knightMoves.add(new ChessMove(myPosition, curr_pos, null));
                }
            }
        }

        row = myPosition.getRow();
        col = myPosition.getColumn();

        // Down-Right
        row = row-2;
        col++;
        if (row > 0 && col < 9) {
            ChessPosition curr_pos = new ChessPosition(row, col);
            ChessPiece curr_piece = board.getPiece(curr_pos);

            // Position is not occupied by a piece
            if (curr_piece == null) {

                knightMoves.add(new ChessMove(myPosition, curr_pos, null));

            // Position is occupied
            } else {
                ChessGame.TeamColor curr_team_color = curr_piece.getTeamColor();
                // Capture
                if (myTeamColor != curr_team_color) {
                    knightMoves.add(new ChessMove(myPosition, curr_pos, null));
                }
            }
        }

        row = myPosition.getRow();
        col = myPosition.getColumn();

        // Down-Left
        row = row-2;
        col--;
        if (row > 0 && col > 0) {
            ChessPosition curr_pos = new ChessPosition(row, col);
            ChessPiece curr_piece = board.getPiece(curr_pos);

            // Position is not occupied by a piece
            if (curr_piece == null) {

                knightMoves.add(new ChessMove(myPosition, curr_pos, null));

            // Position is occupied
            } else {
                ChessGame.TeamColor curr_team_color = curr_piece.getTeamColor();
                // Capture
                if (myTeamColor != curr_team_color) {
                    knightMoves.add(new ChessMove(myPosition, curr_pos, null));
                }
            }
        }

        row = myPosition.getRow();
        col = myPosition.getColumn();

        // Left-Down
        row--;
        col = col-2;
        if (row > 0 && col > 0) {
            ChessPosition curr_pos = new ChessPosition(row, col);
            ChessPiece curr_piece = board.getPiece(curr_pos);

            // Position is not occupied by a piece
            if (curr_piece == null) {

                knightMoves.add(new ChessMove(myPosition, curr_pos, null));

            // Position is occupied
            } else {
                ChessGame.TeamColor curr_team_color = curr_piece.getTeamColor();
                // Capture
                if (myTeamColor != curr_team_color) {
                    knightMoves.add(new ChessMove(myPosition, curr_pos, null));
                }
            }
        }


        row = myPosition.getRow();
        col = myPosition.getColumn();

        // Left-Up
        row++;
        col = col-2;
        if (row < 9 && col > 0) {
            ChessPosition curr_pos = new ChessPosition(row, col);
            ChessPiece curr_piece = board.getPiece(curr_pos);

            // Position is not occupied by a piece
            if (curr_piece == null) {

                knightMoves.add(new ChessMove(myPosition, curr_pos, null));

                // Position is occupied
            } else {
                ChessGame.TeamColor curr_team_color = curr_piece.getTeamColor();
                // Capture
                if (myTeamColor != curr_team_color) {
                    knightMoves.add(new ChessMove(myPosition, curr_pos, null));
                }
            }
        }

        row = myPosition.getRow();
        col = myPosition.getColumn();

        // Up-Left
        row = row+2;
        col--;
        if (row < 9 && col > 0) {
            ChessPosition curr_pos = new ChessPosition(row, col);
            ChessPiece curr_piece = board.getPiece(curr_pos);

            // Position is not occupied by a piece
            if (curr_piece == null) {

                knightMoves.add(new ChessMove(myPosition, curr_pos, null));

                // Position is occupied
            } else {
                ChessGame.TeamColor curr_team_color = curr_piece.getTeamColor();
                // Capture
                if (myTeamColor != curr_team_color) {
                    knightMoves.add(new ChessMove(myPosition, curr_pos, null));
                }
            }
        }

        return knightMoves;
    }
}
