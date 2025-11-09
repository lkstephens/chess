package client;

import datamodel.LoginRequest;
import datamodel.RegisterRequest;
import datamodel.RegisterResult;
import org.junit.jupiter.api.*;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clearDB() throws Exception {
        facade.clear();
    }


    @Test
    public void registerSuccess() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("user", "pass", "email@email.com");
        RegisterResult registerResult = facade.register(registerRequest);
        assertEquals(registerRequest.username(), registerResult.username());
        assertNotNull(registerResult.authToken());
        assertInstanceOf(String.class, registerResult.authToken());
    }

    @Test
    public void registerFailure() {
        RegisterRequest registerRequest = new RegisterRequest(null, "pass", "email@email.com");
        assertThrows(ClientBadRequestException.class, () -> facade.register(registerRequest));
    }

    @Test
    public void loginSuccess() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("user", "pass", "email@email.com");
        RegisterResult registerResult = facade.register(registerRequest);
        facade.logout(registerResult.authToken());
        LoginRequest loginRequest = new LoginRequest("user", "pass");
        assertDoesNotThrow(() -> facade.login(loginRequest));
    }

    @Test
    public void loginFailure() throws Exception{
        LoginRequest loginRequest = new LoginRequest("user", "pass");
        // User not registered yet
        assertThrows(ClientUnauthorizedException.class, () -> facade.login(loginRequest));
        RegisterRequest registerRequest = new RegisterRequest("user", "pass", "email@email.com");
        facade.register(registerRequest);
        assertThrows(ClientBadRequestException.class, () ->
                facade.login(new LoginRequest("user", null)));
        assertThrows(ClientUnauthorizedException.class, () ->
                facade.login(new LoginRequest("user", "bad_pass")));
    }

}
