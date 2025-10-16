package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.*;
import io.javalin.http.Context;
import datamodel.RegisterRequest;
import datamodel.RegisterResult;
import service.BadRequestException;
import service.UserService;

import java.util.Map;

public class Server {

    private final Javalin server;
    private final UserService userService;

    public Server() {
        userService = new UserService();
        server = Javalin.create(config -> config.staticFiles.add("web"));

        server.delete("db", ctx -> ctx.result("{}"));
        server.post("user", this::register);

        // Register your endpoints and exception handlers here.

    }

    private void register(Context ctx) {
        var serializer = new Gson();
        try {
            var request = serializer.fromJson(ctx.body(), RegisterRequest.class);
            RegisterResult result = userService.register(request);
            ctx.result(serializer.toJson(result));
            ctx.status(200);
        } catch(BadRequestException e) {
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

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
