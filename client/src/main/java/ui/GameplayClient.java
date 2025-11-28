package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import client.ServerFacade;
import client.ServerMessageObserver;
import client.WebSocketFacade;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.*;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;
import static ui.EscapeSequences.SET_TEXT_COLOR_MAGENTA;

public class GameplayClient implements ChessClient, ServerMessageObserver {

    private int gameID;
    private ChessGame game;
    private final String gameName;
    private ChessBoard board;
    private final String clientColor;
    private final String authToken;

    private final ServerFacade server;
    private final WebSocketFacade webSocket;



    public GameplayClient(int gameID, ChessGame game, String gameName, String color, ServerFacade server, String authToken) {
        this.gameID = gameID;
        this.game = game;
        this.gameName = gameName;
        board = game.getBoard();
        clientColor = color;
        this.server = server;
        this.authToken = authToken;

        webSocket = new WebSocketFacade(this.server.getServerURL(), this);

        webSocket.connect(this.authToken, gameID);
    }

    @Override
    public String run() {
        System.out.print("\n" + help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("leave")) {
            printPrompt();
            String line = scanner.nextLine();

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
        return result;
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
            if (clientColor.equals("WHITE")) {
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

                    return (clientColor.equals("WHITE")) ? PostLoginClient.drawBoardWhite(board, highlightPosArray) :
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

                ChessMove move = new ChessMove(startPosition, endPosition, null);
                webSocket.makeMove(authToken, gameID, move);
                return "";
            }
        }
        return SET_TEXT_COLOR_RED + "Expected: <a-h1-8> <a-h1-8>";
    }

    public String resign(String... params) {
        return "resigning";
    }

    public String leave(String... params) {
        // does this need to remove the user from the game?
        return "leave";
    }

    private boolean validateCoordinates(String... coordinateArray) {

        for (String coordinate : coordinateArray) {
            coordinate = coordinate.toUpperCase();
            if (coordinate.length() != 2) {
                return false;
            } else {
                char rowChar = coordinate.charAt(0);
                int colInt = Character.getNumericValue(coordinate.charAt(1));

                if (rowChar < 'A' || rowChar > 'H' || colInt < 1 || colInt > 8) {
                    return false;
                }
            }
        }
        return true;
    }

    private ChessPosition convertToPosition(String coordinate) {
        coordinate = coordinate.toUpperCase();
        int row = Character.getNumericValue(coordinate.charAt(1));
        char colLetter = coordinate.charAt(0);
        int col = colLetter - 'A' + 1;

        return new ChessPosition(row, col);
    }

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case NOTIFICATION:
                NotificationMessage notification = (NotificationMessage) message;
                System.out.println();
                System.out.println(SET_TEXT_COLOR_BLUE + notification.getMessage() + RESET_TEXT_COLOR);
                printPrompt();
                break;
            case LOAD_GAME:
                LoadGameMessage loadGameMessage = (LoadGameMessage) message;
                this.game = loadGameMessage.getGame();
                this.board = game.getBoard();
                System.out.print("\n\n");
                if (clientColor.equals("WHITE")) {
                    System.out.println(PostLoginClient.drawBoardWhite(board));
                } else {
                    System.out.println(PostLoginClient.drawBoardBlack(board));
                }
                System.out.print(help());
                printPrompt();
                break;
            case ERROR:
                ErrorMessage errorMessage = (ErrorMessage) message;
                System.out.println();
                System.out.println(SET_TEXT_COLOR_RED + errorMessage.getErrorMessage() + RESET_TEXT_COLOR);
                printPrompt();
                break;
        }
    }

}
