package ui;

import chess.ChessBoard;
import chess.ChessGame;
import client.ServerFacade;
import client.ServerMessageObserver;
import client.WebSocketFacade;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ObserverClient implements ChessClient, ServerMessageObserver {

    private int gameID;
    private ChessGame gameObserving;
    private ChessBoard boardObserving;
    private String gameName;
    private String authToken;

    private final ServerFacade server;
    private final WebSocketFacade webSocket;

    public ObserverClient(int gameID, ChessGame game, String gameName, String authToken, ServerFacade server) {
        this.gameID = gameID;
        gameObserving = game;
        boardObserving = gameObserving.getBoard();
        this.gameName = gameName;
        this.authToken = authToken;
        this.server = server;
        webSocket = new WebSocketFacade(this.server.getServerURL(), this);
        webSocket.connect(this.authToken, gameID);
    }

    @Override
    public String run() {
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
        System.out.print("\n" + RESET_TEXT_COLOR + "[OBSERVING " + gameName + "] >>> ");
    }

    @Override
    public String help() {
        return
                RESET_TEXT_COLOR
              + "Type"
              + SET_TEXT_COLOR_BLUE + " leave "
              + RESET_TEXT_COLOR + "to stop observing " + "\n";
    }

    @Override
    public String eval(String input) {
        String[] tokens = input.toLowerCase().split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

        return switch (cmd) {
            case "leave" -> leave(params);
            default -> "\n" + help();
        };
    }

    public String leave(String... params) {
        if (params.length == 0) {
            webSocket.leave(authToken, gameID);
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
                break;
            case LOAD_GAME:
                LoadGameMessage loadGameMessage = (LoadGameMessage) message;
                this.gameObserving = loadGameMessage.getGame();
                this.boardObserving = gameObserving.getBoard();
                System.out.print("\n\n");
                System.out.println(PostLoginClient.drawBoardWhite(boardObserving));
                break;
            case ERROR:
                ErrorMessage errorMessage = (ErrorMessage) message;
                System.out.println();
                System.out.println(SET_TEXT_COLOR_RED + errorMessage.getErrorMessage() + RESET_TEXT_COLOR);
                break;
        }
        printPrompt();
    }
}
