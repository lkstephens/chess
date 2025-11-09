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
    public void listGamesSuccess() throws Exception {
        RegisterResult registerResult = facade.register(new RegisterRequest("user", "pass", "email@email.com"));
        ListGamesResult listGames = facade.listGames(registerResult.authToken());
        assertTrue(listGames.games().isEmpty());
        // Add 3 games
        facade.createGame(registerResult.authToken(), new CreateGameRequest("game1"));
        facade.createGame(registerResult.authToken(), new CreateGameRequest("game2"));
        facade.createGame(registerResult.authToken(), new CreateGameRequest("game3"));

        listGames = facade.listGames(registerResult.authToken());

        assertEquals(3, listGames.games().size());
        assertEquals(1, listGames.games().getFirst().gameID());
        assertEquals(2, listGames.games().get(1).gameID());
        assertEquals(3, listGames.games().get(2).gameID());
    }

    @Test
    public void listGamesFailure() throws Exception {
        facade.register(new RegisterRequest("user", "pass", "email@email.com"));
        assertThrows(ClientUnauthorizedException.class, () -> facade.listGames("bad_auth"));
    }

    @Test
    public void createGameSuccess() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("user", "pass", "email@email.com");
        RegisterResult registerResult = facade.register(registerRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest("game"); // gameID = 1
        CreateGameResult createGameResult = facade.createGame(registerResult.authToken(), createGameRequest);
        assertEquals(1, createGameResult.gameID());

        assertDoesNotThrow(() -> facade.createGame(registerResult.authToken(), createGameRequest));
    }

    @Test
    public void createGameFailure() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("user", "pass", "email@email.com");
        RegisterResult registerResult = facade.register(registerRequest);
        assertThrows(ClientBadRequestException.class,
                () -> facade.createGame(registerResult.authToken(), new CreateGameRequest("")));
        assertThrows(ClientBadRequestException.class,
                () -> facade.createGame(registerResult.authToken(), new CreateGameRequest(null)));
        assertThrows(ClientUnauthorizedException.class,
                () -> facade.createGame("bad_auth", new CreateGameRequest("game")));
    }

    @Test
    public void joinGameSuccess() throws Exception {
        // Register User1
        RegisterResult user1Auth = facade.register(new RegisterRequest("user1", "pass1", "user1@email.com"));
        // Create 2 games
        facade.createGame(user1Auth.authToken(), new CreateGameRequest("Game_A"));
        facade.createGame(user1Auth.authToken(), new CreateGameRequest("Game_B"));
        // User1 joins Game_A as both WHITE and BLACK
        facade.joinGame(user1Auth.authToken(), new JoinGameRequest("WHITE", 1));
        assertDoesNotThrow(
                () -> facade.joinGame(user1Auth.authToken(), new JoinGameRequest("BLACK", 1)));
        // User1 joins Game_B as WHITE
        facade.joinGame(user1Auth.authToken(), new JoinGameRequest("WHITE", 2));
        // Register User2
        RegisterResult user2Auth = facade.register(new RegisterRequest("user2", "pass2", "user2@email.com"));
        // User2 joins Game_B as BLACK
        facade.joinGame(user2Auth.authToken(), new JoinGameRequest("BLACK", 2));

        ListGamesResult listGames = facade.listGames(user2Auth.authToken());

        // Make sure User1 and User2 are in the correct spots
        assertEquals(user1Auth.username(), listGames.games().getFirst().whiteUsername());
        assertEquals(user1Auth.username(), listGames.games().getFirst().blackUsername());
        assertEquals(user1Auth.username(), listGames.games().get(1).whiteUsername());
        assertEquals(user2Auth.username(), listGames.games().get(1).blackUsername());
    }

    @Test
    public void joinGameFailure() throws Exception {
        RegisterResult registerResult = facade.register(new RegisterRequest("user", "pass", "user@email.com"));
        facade.createGame(registerResult.authToken(), new CreateGameRequest("game"));
        assertThrows(ClientBadRequestException.class,
                () -> facade.joinGame(registerResult.authToken(), new JoinGameRequest(null, 1)));
        assertThrows(ClientBadRequestException.class,
                () -> facade.joinGame(registerResult.authToken(), new JoinGameRequest("WHITE", 2)));
    }
}
