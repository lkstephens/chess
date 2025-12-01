package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import client.ServerFacade;
import client.ServerMessageObserver;
import client.WebSocketFacade;
import datamodel.GameData;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;
import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

public abstract class BaseGameClient implements ServerMessageObserver {

    final int gameID;
    ChessGame game;
    final String gameName;
    ChessBoard board;
    final String authToken;

    final ServerFacade server;
    final WebSocketFacade webSocket;

    boolean gameIsOver = false;

    public BaseGameClient(GameData gameData, ServerFacade server, String authToken) {
        this.gameID = gameData.gameID();
        this.game = gameData.game();
        this.gameName = gameData.gameName();
        this.board = game.getBoard();
        this.server = server;
        this.authToken = authToken;

        this.webSocket = new WebSocketFacade(this.server.getServerURL(), this);
        this.webSocket.connect(this.authToken, gameID);
    }

    public abstract String run();

    String repl() {
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

    public abstract void printPrompt();

    public abstract String eval(String input);

    String highlight(String... coordinates) {

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

                    return drawBoardHighlight(highlightPosArray);

                } else {
                    return SET_TEXT_COLOR_RED + "No piece at " + coordinate + ".";
                }
            }
        }

        return SET_TEXT_COLOR_RED + "Expected <a-h1-8>";

    }

    String leave(String... params) {
        if (params.length == 0) {
            webSocket.leave(authToken, gameID);
            webSocket.closeSession();
            return "leave";
        }
        return SET_TEXT_COLOR_RED + "Expected no parameters for \"leave\"";
    }

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case NOTIFICATION:
                NotificationMessage notification = (NotificationMessage) message;
                System.out.println();
                System.out.println(SET_TEXT_COLOR_BLUE + notification.getMessage() + RESET_TEXT_COLOR);
                if (notification.getMessage().contains("GAME OVER")) {
                    gameIsOver = true;
                    webSocket.closeSession();
                }
                break;
            case LOAD_GAME:
                loadGameHandler((LoadGameMessage) message);
                break;
            case ERROR:
                ErrorMessage errorMessage = (ErrorMessage) message;
                System.out.println();
                System.out.println(SET_TEXT_COLOR_RED + errorMessage.getErrorMessage() + RESET_TEXT_COLOR);
                break;
        }
        printPrompt();
    }

    void loadGameHandler(LoadGameMessage message) {
        this.game = message.getGame();
        this.board = game.getBoard();
        System.out.print("\n\n");

        drawBoard();
    }

    abstract void drawBoard();

    abstract String drawBoardHighlight(ChessPosition[] highlightPosArray);

    boolean validateCoordinates(String... coordinateArray) {

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

    ChessPosition convertToPosition(String coordinate) {
        coordinate = coordinate.toUpperCase();
        int row = Character.getNumericValue(coordinate.charAt(1));
        char colLetter = coordinate.charAt(0);
        int col = colLetter - 'A' + 1;

        return new ChessPosition(row, col);
    }
}
