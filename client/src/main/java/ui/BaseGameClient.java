package ui;

import chess.ChessBoard;
import chess.ChessGame;
import client.ServerFacade;
import client.ServerMessageObserver;
import client.WebSocketFacade;
import datamodel.GameData;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

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

    public abstract void printPrompt();

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
}
