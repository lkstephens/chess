package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import datamodel.GameData;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;

import service.BadRequestException;
import service.GameService;
import service.UnauthorizedException;
import service.UserService;
import websocket.commands.ConnectCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

import java.io.IOException;
import java.util.Collection;

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
    public void handleMessage(@NotNull WsMessageContext ctx) throws IOException {
        Session session = ctx.session;

        try {

            UserGameCommand command = serializer.fromJson(ctx.message(), UserGameCommand.class);
            String username = userService.getUsername(command.getAuthToken());

            switch (command.getCommandType()) {
                case CONNECT -> {
                    ConnectCommand connectCommand = serializer.fromJson(ctx.message(), ConnectCommand.class);
                    connect(session, username, connectCommand);
                }
                case MAKE_MOVE -> {
                    MakeMoveCommand makeMoveCommand = serializer.fromJson(ctx.message(), MakeMoveCommand.class);
                    makeMove(session, username, makeMoveCommand);
                }
            }

        } catch (UnauthorizedException ex) {
            ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                    "Error: user not authorized.");
            session.getRemote().sendString(serializer.toJson(errorMessage));
        } catch (DataAccessException ex) {
            ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                    "Error: could not access chess game.");
            session.getRemote().sendString(serializer.toJson(errorMessage));
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
                throw new DataAccessException("Error: game not found");
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

        } catch (DataAccessException ex) {
            ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                    "Error: failed to connect to chess game. Please enter the number of an existing game.");
            session.getRemote().sendString(serializer.toJson(errorMessage));
        }
    }

    private void makeMove(Session session, String username, MakeMoveCommand command) throws IOException {

        int gameID = command.getGameID();

        try {
            GameData gameData = gameService.getGame(gameID);

            if (gameData == null) {
                throw new DataAccessException("Error: game not found");
            }

            ChessGame game = gameData.game();
            ChessGame.TeamColor teamTurn = game.getTeamTurn();

            ChessGame.TeamColor playerColor;
            if (username.equals(gameData.whiteUsername())) {
                playerColor = ChessGame.TeamColor.WHITE;
            } else if (username.equals(gameData.blackUsername())) {
                playerColor = ChessGame.TeamColor.BLACK;
            } else {
                throw new UnauthorizedException("Error: only players can make a move");
            }

            if (playerColor == teamTurn) {

                ChessMove move = command.getMove();
                game.makeMove(move);
                if (teamTurn == ChessGame.TeamColor.WHITE) {
                    game.setTeamTurn(ChessGame.TeamColor.BLACK);
                } else {
                    game.setTeamTurn(ChessGame.TeamColor.WHITE);
                }

                ChessPosition startPosition = move.getStartPosition();
                ChessPosition endPosition = move.getEndPosition();

                LoadGameMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
                connections.broadcast(gameID, null, loadGameMessage);

                var message = String.format("%s moved [piece] from [start] to [end]", username);
                var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
                connections.broadcast(gameID, session, notification);

            } else {
                throw new UnauthorizedException("Error: it is not your turn");
            }

        } catch (InvalidMoveException ex) {
            ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                    "Error: Invalid move.");
            session.getRemote().sendString(serializer.toJson(errorMessage));
        } catch (UnauthorizedException ex) {
            ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, ex.getMessage());
            session.getRemote().sendString(serializer.toJson(errorMessage));
        } catch (DataAccessException ex) {
            ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                    "Error: failed to connect to chess game, move was not made.");
            session.getRemote().sendString(serializer.toJson(errorMessage));
        }

    }
}
