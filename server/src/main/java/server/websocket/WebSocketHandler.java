package server.websocket;

import com.google.gson.Gson;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;

import service.GameService;
import service.UserService;
import websocket.commands.ConnectCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final UserService userService;
    private final GameService gameService;

    public WebSocketHandler(UserService userService, GameService gameService) {
        this.userService = userService;
        this.gameService = gameService;
    }

    private final ConnectionManager connections = new ConnectionManager();

    @Override
    public void handleConnect(@NotNull WsConnectContext ctx) {
        System.out.println("WebSocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) throws Exception {
        Session session = ctx.session;
        var serializer = new Gson();

        try {

            UserGameCommand command = serializer.fromJson(ctx.message(), UserGameCommand.class);
            String username = userService.getUsername(command.getAuthToken());

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, (ConnectCommand) command);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) {
        System.out.println("WebSocket closed");
    }

    private void connect(Session session, String username, ConnectCommand command) throws IOException {
        int gameID = command.getGameID();
        connections.add(gameID, session);
        var message = String.format("%s has joined the game as.", username);
        var notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(gameID, null, notificationMessage);
    }
}
