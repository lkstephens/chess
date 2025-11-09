package client;

import datamodel.*;
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
    public void clearSuccess() throws Exception {
        // Add data to all three tables
        RegisterRequest registerRequest = new RegisterRequest("user", "pass", "email@email.com");
        RegisterResult registerResult = facade.register(registerRequest);
        facade.createGame(registerResult.authToken(), new CreateGameRequest("game"));
        ListGamesResult listGamesResult = facade.listGames(registerResult.authToken());
        assertEquals(1, listGamesResult.games().size());

        facade.clear();
        // Can register again
        registerResult = assertDoesNotThrow(() -> facade.register(registerRequest));
        listGamesResult = facade.listGames(registerResult.authToken());
        // No games listed
        assertTrue(listGamesResult.games().isEmpty());

        facade.clear();
        // Can clear again
        assertDoesNotThrow(() -> facade.clear());
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

    @Test
    public void logoutSuccess() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("user", "pass", "email@email.com");
        RegisterResult registerResult = facade.register(registerRequest);
        assertDoesNotThrow(() -> facade.logout(registerResult.authToken()));
    }

    @Test
    public void logoutFailure() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("user", "pass", "email@email.com");
        RegisterResult registerResult = facade.register(registerRequest);
        assertThrows(ClientUnauthorizedException.class, () -> facade.logout(registerResult.authToken() + "bad_token"));
    }

    @Test
    public void listGamesSuccess() {

    }

    @Test
    public void createGame() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("user", "pass", "email@email.com");
        RegisterResult registerResult = facade.register(registerRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest("game"); // gameID = 1
        CreateGameResult createGameResult = facade.createGame(registerResult.authToken(), createGameRequest);
        assertEquals(1, createGameResult.gameID());

        assertDoesNotThrow(() -> facade.createGame(registerResult.authToken(), createGameRequest));
    }
}
