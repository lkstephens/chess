package server;

import com.google.gson.Gson;
import dataaccess.*;
import datamodel.*;
import io.javalin.*;
import io.javalin.http.Context;
import server.websocket.WebSocketHandler;
import service.*;

import java.util.Map;

public class Server {

    private final Javalin server;
    private final UserService userService;
    private final GameService gameService;
    private final WebSocketHandler websocketHandler;

    public Server() {

        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        try {
            userDAO = new SQLUserDAO();
            authDAO = new SQLAuthDAO();
            gameDAO = new SQLGameDAO();
        } catch (DataAccessException e) {
            System.err.print("\nError: Server could not connect to database\n");
        }

        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(gameDAO, authDAO);

        server = Javalin.create(config -> config.staticFiles.add("web"));
        websocketHandler = new WebSocketHandler();

        // Endpoints and handlers
        server.delete("db", this::clear);
        server.post("user", this::register);
        server.post("session", this::login);
        server.delete("session", this::logout);
        server.get("game", this::listGames);
        server.post("game", this::createGame);
        server.put("game", this::joinGame);
        server.ws("/ws", ws -> {
            ws.onConnect(websocketHandler);
            ws.onMessage(websocketHandler);
            ws.onClose(websocketHandler);
        });

    }

    private void clear(Context ctx) {
        var serializer = new Gson();
        try {

            userService.clear();
            gameService.clear();
            ctx.result("{}");

        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.result(serializer.toJson(Map.of("message",e.getMessage())));
        }
    }

    private void register(Context ctx) {
        var serializer = new Gson();
        var request = serializer.fromJson(ctx.body(), RegisterRequest.class);
        try {

            RegisterResult result = userService.register(request);
            ctx.result(serializer.toJson(result));

        } catch (BadRequestException e) {
            ctx.status(400);
            ctx.result(serializer.toJson(Map.of("message", e.getMessage())));
        } catch (AlreadyTakenException e) {
            ctx.status(403);
            ctx.result(serializer.toJson(Map.of("message", e.getMessage())));
        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.result(serializer.toJson(Map.of("message",e.getMessage())));
        }
    }

    private void login(Context ctx) {
        var serializer = new Gson();
        var request = serializer.fromJson(ctx.body(), LoginRequest.class);
        try {

            LoginResult result = userService.login(request);
            ctx.result(serializer.toJson(result));

        } catch (BadRequestException e) {
            ctx.status(400);
            ctx.result(serializer.toJson(Map.of("message", e.getMessage())));
        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.result(serializer.toJson(Map.of("message", e.getMessage())));
        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.result(serializer.toJson(Map.of("message", e.getMessage())));
        }
    }

    private void logout(Context ctx) {
        var serializer = new Gson();
        String authToken = ctx.header("authorization");
        try {

            userService.logout(authToken);
            ctx.result("{}");

        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.result(serializer.toJson(Map.of("message", e.getMessage())));
        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.result(serializer.toJson(Map.of("message", e.getMessage())));
        }
    }

    private void listGames(Context ctx) {
        var serializer = new Gson();
        String authToken = ctx.header("authorization");
        try {

            ListGamesResult result = gameService.listGames(authToken);
            ctx.result(serializer.toJson(result));

        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.result(serializer.toJson(Map.of("message", e.getMessage())));
        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.result(serializer.toJson(Map.of("message", e.getMessage())));
        }
    }

    private void createGame(Context ctx) {
        var serializer = new Gson();
        String authToken = ctx.header("authorization");
        var request = serializer.fromJson(ctx.body(), CreateGameRequest.class);
        try {

            CreateGameResult result = gameService.createGame(authToken, request);
            ctx.result(serializer.toJson(result));

        } catch (BadRequestException e) {
            ctx.status(400);
            ctx.result(serializer.toJson(Map.of("message", e.getMessage())));
        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.result(serializer.toJson(Map.of("message", e.getMessage())));
        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.result(serializer.toJson(Map.of("message", e.getMessage())));
        }
    }

    private void joinGame(Context ctx) {
        var serializer = new Gson();
        String authToken = ctx.header("authorization");
        var request = serializer.fromJson(ctx.body(), JoinGameRequest.class);
        try{

            gameService.joinGame(authToken, request);
            ctx.result("{}");

        } catch (BadRequestException e) {
            ctx.status(400);
            ctx.result(serializer.toJson(Map.of("message", e.getMessage())));
        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.result(serializer.toJson(Map.of("message", e.getMessage())));
        } catch (AlreadyTakenException e) {
            ctx.status(403);
            ctx.result(serializer.toJson(Map.of("message", e.getMessage())));
        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.result(serializer.toJson(Map.of("message", e.getMessage())));
        }
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
