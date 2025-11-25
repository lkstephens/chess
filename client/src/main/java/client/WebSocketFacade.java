package client;

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
                    observer.notify(serverMessage);
                }
            });

        } catch (DeploymentException | IOException | URISyntaxException ex) {
            System.out.print("Error setting up WebSocket <-- [CHANGEME]");
        }
    }

    //Endpoint requires this method
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connect(String authToken, int gameID) {
        try {
            var connectCommand = new ConnectCommand(CONNECT, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(connectCommand));
        } catch (IOException ex) {
            System.out.println("connect ws error");
        }
    }





}
