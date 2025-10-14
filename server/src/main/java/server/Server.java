package server;

import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Context;
import datamodel.RegisterRequest;
import datamodel.RegisterResult;
import org.eclipse.jetty.server.Authentication;
import service.UserService;

public class Server {

    private final Javalin server;
    private UserService userService;

    public Server() {
        userService = new UserService();
        server = Javalin.create(config -> config.staticFiles.add("web"));

        server.delete("db", ctx -> ctx.result("{}"));
        server.post("user", this::register);

        // Register your endpoints and exception handlers here.

    }

    private void register(Context ctx) {
        var serializer = new Gson();
        var request = serializer.fromJson(ctx.body(), RegisterRequest.class);
        RegisterResult result = userService.register(request);
        ctx.result(serializer.toJson(result));
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
