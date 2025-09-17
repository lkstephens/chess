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

        // Team Color: WHITE
        if (myTeamColor == ChessGame.TeamColor.WHITE) {

            // Move up 1
            ChessPosition curr_pos = new ChessPosition(row+1, col);
            ChessPiece curr_piece = board.getPiece(curr_pos);
            if (curr_piece == null) {

                if (row+1 == 8) {
                    pawnMoves.add(new ChessMove(myPosition, curr_pos, ChessPiece.PieceType.QUEEN));
                    pawnMoves.add(new ChessMove(myPosition, curr_pos, ChessPiece.PieceType.ROOK));
                    pawnMoves.add(new ChessMove(myPosition, curr_pos, ChessPiece.PieceType.BISHOP));
                    pawnMoves.add(new ChessMove(myPosition, curr_pos, ChessPiece.PieceType.KNIGHT));
                } else {
                    pawnMoves.add(new ChessMove(myPosition, curr_pos, null));
                }

                // Move Up 2 if still on row 2
                if (row == 2) {
                    curr_pos = new ChessPosition(row+2, col);
                    curr_piece = board.getPiece(curr_pos);
                    if (curr_piece == null) {
                        pawnMoves.add(new ChessMove(myPosition, curr_pos, null));
                    }
                }
            }

            // Capture Up-Right
            if (col < 8) {
                curr_pos = new ChessPosition(row + 1, col + 1);
                curr_piece = board.getPiece(curr_pos);
                if (curr_piece != null && curr_piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
                    // Promotion (Set to Queen for now)
                    if (row + 1 == 8) {
                        pawnMoves.add(new ChessMove(myPosition, curr_pos, ChessPiece.PieceType.QUEEN));
                        pawnMoves.add(new ChessMove(myPosition, curr_pos, ChessPiece.PieceType.ROOK));
                        pawnMoves.add(new ChessMove(myPosition, curr_pos, ChessPiece.PieceType.BISHOP));
                        pawnMoves.add(new ChessMove(myPosition, curr_pos, ChessPiece.PieceType.KNIGHT));
                    } else {
                        pawnMoves.add(new ChessMove(myPosition, curr_pos, null));
                    }
                }
            }

            // Capture Up-Left
            if (col > 1) {
                curr_pos = new ChessPosition(row + 1, col - 1);
                curr_piece = board.getPiece(curr_pos);
                // Promotion (Set to Queen for now)
                if (curr_piece != null && curr_piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
                    if (row + 1 == 8) {
                        pawnMoves.add(new ChessMove(myPosition, curr_pos, ChessPiece.PieceType.QUEEN));
                        pawnMoves.add(new ChessMove(myPosition, curr_pos, ChessPiece.PieceType.ROOK));
                        pawnMoves.add(new ChessMove(myPosition, curr_pos, ChessPiece.PieceType.BISHOP));
                        pawnMoves.add(new ChessMove(myPosition, curr_pos, ChessPiece.PieceType.KNIGHT));
                    } else {
                        pawnMoves.add(new ChessMove(myPosition, curr_pos, null));
                    }
                }
            }

        // Team Color: BLACK
        } else {

            // Move Down 1
            ChessPosition curr_pos = new ChessPosition(row-1, col);
            ChessPiece curr_piece = board.getPiece(curr_pos);
            if (curr_piece == null) {

                if (row-1 == 1) {
                    pawnMoves.add(new ChessMove(myPosition, curr_pos, ChessPiece.PieceType.QUEEN));
                    pawnMoves.add(new ChessMove(myPosition, curr_pos, ChessPiece.PieceType.ROOK));
                    pawnMoves.add(new ChessMove(myPosition, curr_pos, ChessPiece.PieceType.BISHOP));
                    pawnMoves.add(new ChessMove(myPosition, curr_pos, ChessPiece.PieceType.KNIGHT));
                } else {
                    pawnMoves.add(new ChessMove(myPosition, curr_pos, null));
                }

                // Move Down 2 if still on row 7
                if (row == 7) {
                    curr_pos = new ChessPosition(row - 2, col);
                    curr_piece = board.getPiece(curr_pos);
                    if (curr_piece == null) {
                        pawnMoves.add(new ChessMove(myPosition, curr_pos, null));
                    }
                }
            }

            // Capture Down-Right
            if (col < 8) {
                curr_pos = new ChessPosition(row - 1, col + 1);
                curr_piece = board.getPiece(curr_pos);
                if (curr_piece != null && curr_piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    // Promotion
                    if (row - 1 == 1) {
                        pawnMoves.add(new ChessMove(myPosition, curr_pos, ChessPiece.PieceType.QUEEN));
                        pawnMoves.add(new ChessMove(myPosition, curr_pos, ChessPiece.PieceType.ROOK));
                        pawnMoves.add(new ChessMove(myPosition, curr_pos, ChessPiece.PieceType.BISHOP));
                        pawnMoves.add(new ChessMove(myPosition, curr_pos, ChessPiece.PieceType.KNIGHT));
                    } else {
                        pawnMoves.add(new ChessMove(myPosition, curr_pos, null));
                    }
                }
            }

            // Capture Down-Left
            if (col > 1) {
                curr_pos = new ChessPosition(row - 1, col - 1);
                curr_piece = board.getPiece(curr_pos);
                // Promotion
                if (curr_piece != null && curr_piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    if (row - 1 == 1) {
                        pawnMoves.add(new ChessMove(myPosition, curr_pos, ChessPiece.PieceType.QUEEN));
                        pawnMoves.add(new ChessMove(myPosition, curr_pos, ChessPiece.PieceType.ROOK));
                        pawnMoves.add(new ChessMove(myPosition, curr_pos, ChessPiece.PieceType.BISHOP));
                        pawnMoves.add(new ChessMove(myPosition, curr_pos, ChessPiece.PieceType.KNIGHT));
                    } else {
                        pawnMoves.add(new ChessMove(myPosition, curr_pos, null));
                    }
                }
            }
        }

        return pawnMoves;

    }
}
