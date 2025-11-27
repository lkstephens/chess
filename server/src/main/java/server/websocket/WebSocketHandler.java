package server.websocket;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import datamodel.GameData;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;

import service.BadRequestException;
import service.GameService;
import service.UserService;
import websocket.commands.ConnectCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final UserService userService;
    private final GameService gameService;
    private final Gson serializer = new Gson();

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

        try {

            UserGameCommand command = serializer.fromJson(ctx.message(), UserGameCommand.class);
            String username = userService.getUsername(command.getAuthToken());

            switch (command.getCommandType()) {
                case CONNECT -> {
                    ConnectCommand connectCommand = serializer.fromJson(ctx.message(), ConnectCommand.class);
                    connect(session, username, connectCommand);
                }
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
        try {
            GameData gameData = gameService.getGame(gameID);

            if (gameData == null) {
                throw new BadRequestException("Error: game not found");
            }

            String playerColor = "OBSERVER";
            if (username.equals(gameData.whiteUsername())) {
                playerColor = "WHITE";
            } else if (username.equals(gameData.blackUsername())) {
                playerColor = "BLACK";
            }

            connections.add(gameID, session);

            LoadGameMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME,
                    gameData.game());
            session.getRemote().sendString(serializer.toJson(loadGameMessage));

            var message = String.format("%s has joined the game as %s.", username, playerColor);
            var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);

            connections.broadcast(gameID, session, notification);

        } catch (BadRequestException | DataAccessException ex) {
            ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                    "Error: failed to connect to chess game. Please enter the number of an existing game.");
            session.getRemote().sendString(serializer.toJson(errorMessage));
        }
    }
}
