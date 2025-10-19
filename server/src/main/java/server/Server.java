package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import datamodel.*;
import io.javalin.*;
import io.javalin.http.Context;
import service.BadRequestException;
import service.GameService;
import service.UnauthorizedException;
import service.UserService;

import java.util.Map;

public class Server {

    private final Javalin server;
    private final UserService userService;
    private final GameService gameService;

    public Server() {
        userService = new UserService();
        // Get the same AuthDAO as the one that exists in the UserService
        gameService = new GameService(userService.getAuthDAO());
        server = Javalin.create(config -> config.staticFiles.add("web"));

        server.delete("db", this::clear);
        server.post("user", this::register);
        server.post("session", this::login);
        server.delete("session", this::logout);
        server.get("game", this::listGames);
        server.post("game", this::createGame);
        server.put("game", this::joinGame);

        // Register your endpoints and exception handlers here.

    }

    private void clear(Context ctx) {
        userService.clear();
        gameService.clear();
        ctx.result("{}");
        ctx.status(200);
    }

    private void register(Context ctx) {
        var serializer = new Gson();
        var request = serializer.fromJson(ctx.body(), RegisterRequest.class);
        try {

            RegisterResult result = userService.register(request);
            ctx.result(serializer.toJson(result));
            ctx.status(200);

        } catch (BadRequestException e) {
            ctx.status(400);
            ctx.result(serializer.toJson(Map.of("message", e.getMessage())));
        } catch (UserService.AlreadyTakenException e) {
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
            ctx.status(200);

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
            ctx.status(200);

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
            ctx.status(200);

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
            ctx.status(200);

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
            ctx.status(200);

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

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
