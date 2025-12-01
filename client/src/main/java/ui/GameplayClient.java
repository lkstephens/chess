package ui;

import chess.*;
import client.ServerFacade;
import datamodel.GameData;

import java.util.*;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;
import static ui.EscapeSequences.SET_TEXT_COLOR_MAGENTA;

public class GameplayClient extends BaseGameClient implements ChessClient {

    private final ChessGame.TeamColor clientColor;

    public GameplayClient(GameData gameData, ChessGame.TeamColor color, ServerFacade server, String authToken) {
        super(gameData, server, authToken);
        this.clientColor = color;
    }

    @Override
    public String run() {
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("leave") && !gameIsOver) {
            printPrompt();
            String line = scanner.nextLine();

            if (gameIsOver) {
                break;
            }

            try {
                result = eval(line);
                if (!result.equals("leave")) {
                    System.out.print(result);
                }

            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        return result.equals("leave") ? "leave" : "gameover";
    }

    @Override
    public void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + "["+gameName+"] " + ">>> ");
    }

    @Override
    public String help() {
        return
                SET_TEXT_COLOR_BLUE + "redraw"
              + RESET_TEXT_COLOR + " - chess board\n"

              + SET_TEXT_COLOR_BLUE + "highlight "
              + SET_TEXT_COLOR_MAGENTA + "<a-h1-8>"
              + RESET_TEXT_COLOR + " - legal moves\n"

              + SET_TEXT_COLOR_BLUE + "makemove "
              + SET_TEXT_COLOR_MAGENTA + "<a1> <a1>"
              + RESET_TEXT_COLOR + " - to make a move\n"

              + SET_TEXT_COLOR_BLUE + "resign"
              + RESET_TEXT_COLOR + " - to forfeit game\n"

              + SET_TEXT_COLOR_BLUE + "leave"
              + RESET_TEXT_COLOR + " - game\n"

              + SET_TEXT_COLOR_BLUE + "help "
              + RESET_TEXT_COLOR + " - with possible commands\n";
    }

    @Override
    public String eval(String input) {
        String[] tokens = input.toLowerCase().split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

        return switch (cmd) {
            case "redraw" -> redrawBoard(params);
            case "highlight" -> highlight(params);
            case "makemove" -> makeMove(params);
            case "resign" -> resign(params);
            case "leave" -> leave(params);
            default -> "\n" + help();
        };
    }

    public String redrawBoard(String... params) {
        if (params.length == 0) {
            System.out.println();
            if (clientColor == ChessGame.TeamColor.WHITE) {
                return PostLoginClient.drawBoardWhite(board);
            } else {
                return PostLoginClient.drawBoardBlack(board);
            }
        }

        return SET_TEXT_COLOR_RED + "Expected no parameters for \"redraw\".";
    }

    public String highlight(String... coordinates) {

        if (coordinates.length == 1) {
            String coordinate = coordinates[0];

            if (validateCoordinates(coordinate)) {
                ChessPosition pos = convertToPosition(coordinate);
                if (board.getPiece(pos) != null) {
                    Collection<ChessMove> validMoves = game.validMoves(pos);
                    List<ChessPosition> endPositions = new ArrayList<>();

                    for (ChessMove move : validMoves) {
                        endPositions.add(move.getEndPosition());
                    }
                    // Add start position to endPositions for highlighting
                    endPositions.add(pos);

                    ChessPosition[] highlightPosArray = endPositions.toArray(new ChessPosition[0]);

                    return (clientColor == ChessGame.TeamColor.WHITE) ?
                                                           PostLoginClient.drawBoardWhite(board, highlightPosArray) :
                                                           PostLoginClient.drawBoardBlack(board,highlightPosArray);

                } else {
                    return SET_TEXT_COLOR_RED + "No piece at " + coordinate + ".";
                }
            }
        }

        return SET_TEXT_COLOR_RED + "Expected <a-h1-8>";
    }

    public String makeMove(String... params) {
        if (params.length == 2) {
            if (validateCoordinates(params)) {
                ChessPosition startPosition = convertToPosition(params[0]);
                ChessPosition endPosition = convertToPosition(params[1]);
                ChessPiece piece = game.getBoard().getPiece(startPosition);
                ChessPiece.PieceType promotionPieceType = null;

                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.PAWN &&
                   ((clientColor == ChessGame.TeamColor.WHITE && endPosition.getRow() == 8) ||
                     clientColor == ChessGame.TeamColor.BLACK && endPosition.getRow() == 1)) {
                    promotionPieceType = askPromotionPiece();
                }
                ChessMove move = new ChessMove(startPosition, endPosition, promotionPieceType);
                webSocket.makeMove(authToken, gameID, move);
                return "";
            }
        }
        return SET_TEXT_COLOR_RED + "Expected: <a-h1-8> <a-h1-8>";
    }

    public String resign(String... params) {
        if (params.length == 0) {
            if (confirmResign()) {
                webSocket.resign(authToken, gameID);
            }
            return "";
        }
        return SET_TEXT_COLOR_RED + "Expected no parameters for \"resign\"";
    }

    private ChessPiece.PieceType askPromotionPiece() {
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (true) {
            System.out.print("Enter pawn promotion piece: ");
            result = scanner.nextLine().toUpperCase();

            if (result.equals("QUEEN") || result.equals("ROOK") ||
                result.equals("BISHOP") || result.equals("KNIGHT")) {
                return switch (result) {
                    case "QUEEN" -> ChessPiece.PieceType.QUEEN;
                    case "ROOK" -> ChessPiece.PieceType.ROOK;
                    case "BISHOP" -> ChessPiece.PieceType.BISHOP;
                    case "KNIGHT" -> ChessPiece.PieceType.KNIGHT;
                    default -> null;
                };
            } else {
                System.out.println(SET_TEXT_COLOR_BLUE + "\nValid promotion pieces: [queen|rook|bishop|knight]" +
                                   RESET_TEXT_COLOR);
            }
        }
    }

    private boolean confirmResign() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Are you sure you want to resign? Type \"y\" to confirm: ");
        var result = scanner.nextLine().toUpperCase();
        return result.equals("Y");
    }

    @Override
    void drawBoard() {
        if (clientColor == ChessGame.TeamColor.WHITE) {
            System.out.println(PostLoginClient.drawBoardWhite(board));
        } else {
            System.out.println(PostLoginClient.drawBoardBlack(board));
        }
    }

    @Override
    String drawBoardHighlight(ChessPosition[] highlightPosArray) {
        if (clientColor == ChessGame.TeamColor.WHITE) {
            return PostLoginClient.drawBoardWhite(board);
        } else {
            return PostLoginClient.drawBoardBlack(board);
        }
    }

}
