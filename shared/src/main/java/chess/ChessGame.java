package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn = TeamColor.WHITE;
    private ChessBoard board;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece != null) {
            TeamColor teamColor = piece.getTeamColor();
            Collection<ChessMove> validMoves = piece.pieceMoves(board, startPosition);
            Collection<ChessMove> movesToRemove = new ArrayList<>();
            // Iterate through all moves
            for (ChessMove move : validMoves) {
                // Temporary board copy - Does this work?
                ChessBoard tempBoard = (ChessBoard) board.clone();
                // Make the move
                tempBoard.movePiece(move.getStartPosition(), move.getEndPosition());
                // Does the move leave the king in check?
                if (isInCheckTempBoard(teamColor, tempBoard)) {
                    movesToRemove.add(move);
                }
            }
            // Remove invalid moves
            validMoves.removeAll(movesToRemove);

            return validMoves;

        } else {
            return null;
        }
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {

        Collection<ChessMove> availableMoves = validMoves(move.getStartPosition());

        ChessPiece piece = board.getPiece(move.getStartPosition());

        if (piece != null) {
            TeamColor moveTeamColor = piece.getTeamColor();

            // Is the move valid, and is the move for the correct team
            if (availableMoves.contains(move) && moveTeamColor == getTeamTurn()) {

                // Move the piece
                board.movePiece(move.getStartPosition(), move.getEndPosition());

                // Piece promotion
                if (move.getPromotionPiece() != null) {
                    ChessPiece promotionPiece = new ChessPiece(moveTeamColor, move.getPromotionPiece());
                    board.addPiece(move.getEndPosition(), promotionPiece);
                }

                // Switch teams
                if (getTeamTurn() == TeamColor.WHITE) {
                    setTeamTurn(TeamColor.BLACK);
                } else {
                    setTeamTurn(TeamColor.WHITE);
                }

            } else {
                throw new InvalidMoveException();
            }
        } else {
            throw new InvalidMoveException();
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        // Find the King
        ChessPosition kingPosition = board.getKingLocation(teamColor);

        // Determine if the opposing team can capture the king
        return opposingTeamCapture(teamColor, board, kingPosition);
    }

    public boolean isInCheckTempBoard(TeamColor teamColor, ChessBoard tempBoard) {
        // Find the King on tempBoard
        ChessPosition kingPosition = tempBoard.getKingLocation(teamColor);

        // Determine if the opposing team can capture the king
        return opposingTeamCapture(teamColor, tempBoard, kingPosition);
    }

    public boolean opposingTeamCapture(TeamColor teamColor, ChessBoard board, ChessPosition kingPosition) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition tempPosition = new ChessPosition(row, col);
                ChessPiece tempPiece = board.getPiece(tempPosition);
                if (tempPiece != null) {
                    TeamColor tempPieceColor = tempPiece.getTeamColor();
                    // If it's the opposing team's piece, get its moves
                    if (tempPieceColor != teamColor) {
                        Collection<ChessMove> tempPieceMoves = tempPiece.pieceMoves(board, tempPosition);
                        for (ChessMove move : tempPieceMoves) {
                            if (move.getEndPosition().equals(kingPosition)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }


    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        // King must be IN CHECK
        if (isInCheck(teamColor)) {
            // No valid moves?
            return noValidMoves(teamColor);
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        // King must NOT be IN CHECK
        if (!isInCheck(teamColor)) {
            // No valid moves?
            return noValidMoves(teamColor);
        }
        return false;
    }

    public boolean noValidMoves(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition tempPosition = new ChessPosition(row, col);
                ChessPiece tempPiece = board.getPiece(tempPosition);
                if (tempPiece != null && tempPiece.getTeamColor() == teamColor) {
                    Collection<ChessMove> tempPieceValidMoves = validMoves(tempPosition);
                    // Has a valid move
                    if (!tempPieceValidMoves.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "teamTurn=" + teamTurn +
                ", board=" + board +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return 71 * Objects.hash(teamTurn, board);
    }
}
