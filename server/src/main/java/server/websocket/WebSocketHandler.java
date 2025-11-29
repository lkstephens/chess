package server.websocket;

import chess.*;
import chess.ChessGame.TeamColor;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import datamodel.GameData;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;

import service.*;
import websocket.commands.*;
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
                case LEAVE -> {
                    LeaveCommand leaveCommand = serializer.fromJson(ctx.message(), LeaveCommand.class);
                    leave(session, username, leaveCommand);
                }
                case RESIGN -> {
                    ResignCommand resignCommand = serializer.fromJson(ctx.message(), ResignCommand.class);
                    resign(session, username, resignCommand);
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
            ChessGame.TeamColor opposingColor;
            String opposingUsername;
            if (username.equals(gameData.whiteUsername())) {
                playerColor = ChessGame.TeamColor.WHITE;
                opposingColor = ChessGame.TeamColor.BLACK;
                opposingUsername = gameData.blackUsername();
            } else if (username.equals(gameData.blackUsername())) {
                playerColor = ChessGame.TeamColor.BLACK;
                opposingColor = ChessGame.TeamColor.WHITE;
                opposingUsername = gameData.whiteUsername();
            } else {
                throw new UnauthorizedException("Error: only players can make a move");
            }

            if (game.gameIsOver()) {
                throw new BadRequestException("Error: Game is over. No more moves can be made");
            }

            if (playerColor == teamTurn) {

                ChessMove move = command.getMove();
                ChessPiece piece = game.getBoard().getPiece(move.getStartPosition());

                if (piece != null) {
                    game.makeMove(move); // This already calls setTeamTurn()
                } else {
                    throw new BadRequestException("Error: must have an piece at the start position to make a move");
                }

                gameService.updateGame(gameID, game);

                // Send new board to everyone
                LoadGameMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
                session.getRemote().sendString(serializer.toJson(loadGameMessage));
                connections.broadcast(gameID, session, loadGameMessage);

                // Send move notification to everyone but the person that made the move
                ChessPosition startPosition = move.getStartPosition();
                ChessPosition endPosition = move.getEndPosition();
                String startCoordinate = convertPosToCoordinates(startPosition);
                String endCoordinate = convertPosToCoordinates(endPosition);

                var message = String.format("%s moved %s from %s to %s",
                                            username, piece, startCoordinate, endCoordinate);
                var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
                connections.broadcast(gameID, session, notification);

                // In checkmate notification
                if (game.isInCheckmate(opposingColor)) {
                    gameService.updateGame(gameID, game);

                    var checkmateNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                                    String.format("%s is in checkmate! GAME OVER.", opposingUsername));
                    session.getRemote().sendString(serializer.toJson(checkmateNotification));
                    connections.broadcast(gameID, session, checkmateNotification);
                // In stalemate notification
                } else if (game.isInStalemate(playerColor) || game.isInStalemate(opposingColor)) {
                    gameService.updateGame(gameID, game);

                    var stalemateNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                            "The game has ended in a stalemate. GAME OVER.");
                    session.getRemote().sendString(serializer.toJson(stalemateNotification));
                    connections.broadcast(gameID, session, stalemateNotification);
                // In check notification
                } else if (game.isInCheck(opposingColor)) {
                    var checkNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                            String.format("%s is in check!", opposingUsername));
                    session.getRemote().sendString(serializer.toJson(checkNotification));
                    connections.broadcast(gameID, session, checkNotification);
                }


            } else {
                throw new UnauthorizedException("Error: it is not your turn");
            }

        } catch (BadRequestException | UnauthorizedException ex) {
            ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, ex.getMessage());
            session.getRemote().sendString(serializer.toJson(errorMessage));
        } catch (InvalidMoveException ex) {
            ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                    "Error: Invalid move.");
            session.getRemote().sendString(serializer.toJson(errorMessage));
        } catch (DataAccessException ex) {
            ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR,
                    "Error: failed to connect to chess game, move was not made.");
            session.getRemote().sendString(serializer.toJson(errorMessage));
        }

    }

    private void leave(Session session, String username, LeaveCommand leaveCommand) throws IOException {
        int gameID = leaveCommand.getGameID();
        try {
            GameData gameData = gameService.getGame(gameID);

            if (gameData == null) {
                throw new DataAccessException("Error: game not found");
            }

            ChessGame.TeamColor playerColor = null;
            if (username.equals(gameData.whiteUsername())) {
                playerColor = ChessGame.TeamColor.WHITE;
            } else if (username.equals(gameData.blackUsername())) {
                playerColor = ChessGame.TeamColor.BLACK;
            }

            // Update game if it's a player
            if (playerColor != null) {
                gameService.updateGameUsers(gameID, null, playerColor);
            }

            // Remove session from connection manager whether it's an observer or player
            connections.remove(gameID, session);

            // Broadcast leave message
            var leaveNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    String.format("%s has left the game.", username));
            connections.broadcast(gameID, session, leaveNotification);

        } catch (BadRequestException | DataAccessException ex) {
            ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, ex.getMessage());
            session.getRemote().sendString(serializer.toJson(errorMessage));
        }
    }

    private void resign(Session session, String username, ResignCommand resignCommand) throws IOException {
        int gameID = resignCommand.getGameID();
        try {
            GameData gameData = gameService.getGame(gameID);

            if (gameData == null) {
                throw new DataAccessException("Error: game not found");
            }

            ChessGame.TeamColor playerColor = null;
            if (username.equals(gameData.whiteUsername())) {
                playerColor = ChessGame.TeamColor.WHITE;
            } else if (username.equals(gameData.blackUsername())) {
                playerColor = ChessGame.TeamColor.BLACK;
            }

            if (playerColor != null) {

                // Resign if the game isn't over
                var game = gameData.game();
                if (!game.gameIsOver()) {
                    game.resign(playerColor);
                    gameService.updateGame(gameID, game);
                } else {
                    throw new BadRequestException("The game is over. You may not resign.");
                }

                // Get the winning team username
                TeamColor winningTeamColor = game.getWinningTeamColor();
                String winningUsername;
                if (winningTeamColor == TeamColor.WHITE) {
                    winningUsername = gameData.whiteUsername();
                } else if (winningTeamColor == TeamColor.BLACK) {
                    winningUsername = gameData.blackUsername();
                } else {
                    throw new Exception("Internal server error. Winner not set after resign");
                }

                var resignNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                        String.format("%s has resigned from the game. %s wins!", username, winningUsername));
                session.getRemote().sendString(serializer.toJson(resignNotification));
                connections.broadcast(gameID, session, resignNotification);

            } else {
                throw new BadRequestException("Error: only players can resign");
            }
        } catch (Exception ex) {
            ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, ex.getMessage());
            session.getRemote().sendString(serializer.toJson(errorMessage));
        }
    }

    private String convertPosToCoordinates(ChessPosition pos) {
        int row = pos.getRow();
        int colInt = pos.getColumn();
        char colChar = (char) ('a' + colInt - 1);
        return colChar + Integer.toString(row);
    }
}
