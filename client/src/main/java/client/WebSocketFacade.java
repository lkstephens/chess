package client;

import chess.ChessMove;
import com.google.gson.Gson;
import jakarta.websocket.*;

import websocket.messages.*;
import websocket.commands.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

import static websocket.commands.UserGameCommand.CommandType.*;

public class WebSocketFacade extends Endpoint {

    Session session;
    private ServerMessageObserver observer;
    private final Gson serializer = new Gson();

    public WebSocketFacade(String url, ServerMessageObserver observer) {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.observer = observer;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            // Set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);

                    switch (serverMessage.getServerMessageType()) {
                        case LOAD_GAME:
                            serverMessage = serializer.fromJson(message, LoadGameMessage.class);
                            break;
                        case NOTIFICATION:
                            serverMessage = serializer.fromJson(message, NotificationMessage.class);
                            break;
                        case ERROR:
                            serverMessage = serializer.fromJson(message, ErrorMessage.class);
                            break;
                        default:
                            System.err.println("Unknown ServerMessage Type: " + serverMessage.getServerMessageType());
                    }
                    observer.notify(serverMessage);
                }
            });

        } catch (DeploymentException | IOException | URISyntaxException ex) {
            System.out.print("Error setting up WebSocket");
            ex.printStackTrace();
        }
    }

    //Endpoint requires this method
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connect(String authToken, int gameID) {
        try {
            var connectCommand = new ConnectCommand(CONNECT, authToken, gameID);
            this.session.getBasicRemote().sendText(serializer.toJson(connectCommand));
        } catch (IOException ex) {
            System.out.println("Connection lost"); // <-- CHANGEME
        }
    }

    public void makeMove(String authToken, int gameID, ChessMove move) {
        try {
            var makeMoveCommand = new MakeMoveCommand(MAKE_MOVE, authToken, gameID, move);
            this.session.getBasicRemote().sendText(serializer.toJson(makeMoveCommand));
        } catch (IOException ex) {
            System.out.println("Connection lost"); // <-- CHANGEME?
        }
    }

    public void leave(String authToken, int gameID) {
        try {
            var leaveCommand = new LeaveCommand(LEAVE, authToken, gameID);
            this.session.getBasicRemote().sendText(serializer.toJson(leaveCommand));
        } catch (IOException ex) {
            System.out.println("Connection lost"); // <-- CHANGEME?
        }
    }

    public void closeSession() {
        try {
            if (this.session != null && this.session.isOpen()) {
                this.session.close();
            }
        } catch (IOException ex) {
            System.out.println("Error: could not close ws session");
        }
    }





}
