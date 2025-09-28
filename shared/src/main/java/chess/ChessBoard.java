package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    final private ChessPiece[][] board = new ChessPiece[8][8];

    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow()-1][position.getColumn()-1];
    }

    public void movePiece(ChessPosition startPosition, ChessPosition endPosition) {
        int startRow = startPosition.getRow();
        int startCol = startPosition.getColumn();
        int endRow = endPosition.getRow();
        int endCol = endPosition.getColumn();

        ChessPiece piece = getPiece(startPosition);

        board[startRow-1][startCol-1] = null;
        board[endRow-1][endCol-1] = piece;
    }

    public ChessPosition getKingLocation(ChessGame.TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition tempPosition = new ChessPosition(row, col);
                ChessPiece tempPiece = getPiece(tempPosition);
                if (tempPiece != null) {
                    ChessGame.TeamColor tempPieceColor = tempPiece.getTeamColor();
                    if (teamColor == tempPieceColor && tempPiece.getPieceType() == ChessPiece.PieceType.KING) {
                        return tempPosition;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        // Clear Board
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board.length; col++) {
                board[row][col] = null;
            }
        }

        for (int col = 1; col <= 8; col++) {

            // Add ROOK
            if (col == 1 || col == 8) {
                ChessPiece rook = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
                ChessPosition pos = new ChessPosition(1,col);
                addPiece(pos, rook);

                rook = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
                pos = new ChessPosition(8,col);
                addPiece(pos, rook);
            }

            // Add KNIGHT
            if (col == 2 || col == 7) {
                ChessPiece knight = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
                ChessPosition pos = new ChessPosition(1,col);
                addPiece(pos, knight);

                knight = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
                pos = new ChessPosition(8,col);
                addPiece(pos, knight);
            }

            // Add BISHOP
            if (col == 3 || col == 6) {
                ChessPiece bishop = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
                ChessPosition pos = new ChessPosition(1, col);
                addPiece(pos, bishop);

                bishop = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
                pos = new ChessPosition(8, col);
                addPiece(pos, bishop);
            }

            // Add QUEEN
            if (col == 4) {
                ChessPiece queen = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
                ChessPosition pos = new ChessPosition(1, col);
                addPiece(pos, queen);

                queen = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
                pos = new ChessPosition(8, col);
                addPiece(pos, queen);
            }

            // Add KING
            if (col == 5) {
                ChessPiece king = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
                ChessPosition pos = new ChessPosition(1, col);
                addPiece(pos, king);

                king = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
                pos = new ChessPosition(8, col);
                addPiece(pos, king);
            }


            // Add PAWN
            ChessPiece pawn = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            ChessPosition pos = new ChessPosition(2,col);
            addPiece(pos, pawn);

            pawn = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
            pos = new ChessPosition(7,col);
            addPiece(pos, pawn);

        }

    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "board=" + Arrays.toString(board) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return 71 * Arrays.deepHashCode(board);
    }
}
