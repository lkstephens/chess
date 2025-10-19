package service;

import dataaccess.DataAccessException;
import datamodel.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    @Test
    void clear() {

    }

    @Test
    void registerGood() throws BadRequestException, AlreadyTakenException, DataAccessException {
        var registerRequest = new RegisterRequest("lando", "lando", "lando@java.com");

        var service = new UserService();

        RegisterResult response = service.register(registerRequest);
        assertNotNull(response);
        assertEquals(response.username(), registerRequest.username());
        assertNotNull(response.authToken());
        assertEquals(String.class, response.authToken().getClass());

    }

    @Test
    void registerEmptyFields() {

        var service = new UserService();

        var registerRequest1 = new RegisterRequest("", "test", "testemail@gmail.com");
        var registerRequest2 = new RegisterRequest("test2", "", "testemail@gmail.com");
        var registerRequest3 = new RegisterRequest("test3", "test", "");

        assertThrows(BadRequestException.class, () -> service.register(registerRequest1));

        assertThrows(BadRequestException.class, () -> service.register(registerRequest2));

        assertThrows(BadRequestException.class, () -> service.register(registerRequest3));

    }

    @Test
    void loginGood() throws BadRequestException, AlreadyTakenException, DataAccessException, UnauthorizedException {

        var registerRequest = new RegisterRequest("user", "pass", "user@example.com");
        var loginRequest = new LoginRequest("user", "pass");

        var service = new UserService();

        service.register(registerRequest);
        LoginResult response = service.login(loginRequest);

        assertNotNull(response);
        assertEquals(response.username(), loginRequest.username());
        assertEquals(response.username(),registerRequest.username());
        assertNotNull(response.authToken());
        assertEquals(String.class, response.authToken().getClass());

    }

    @Test
    void loginUserNotCreated() {

        var loginRequest = new LoginRequest("user", "pass");
        var service = new UserService();
        assertThrows(UnauthorizedException.class, () -> service.login(loginRequest));

    }

    @Test
    void loginIncorrectPassword() {

        var registerRequest = new RegisterRequest("user", "pass", "email@domain.com");
        var loginRequest = new LoginRequest("user", "password");
        var service = new UserService();

        assertThrows(UnauthorizedException.class, () -> {
            service.register(registerRequest);
            service.login(loginRequest);
        });

    }

    @Test
    void logoutGood() throws BadRequestException, AlreadyTakenException, DataAccessException, UnauthorizedException {

        var service = new UserService();

        // Register
        var registerRequest = new RegisterRequest("user", "pass", "user@example.com");

        // Logout
        var registerResult = service.register(registerRequest);
        assertEquals(String.class, registerResult.authToken().getClass());
        service.logout(registerResult.authToken());

        // Login
        var loginRequest = new LoginRequest("user", "pass");
        var loginResult = service.login(loginRequest);

        // Logout
        service.logout(loginResult.authToken());

    }

    @Test
    void logoutBadAuth() throws BadRequestException, AlreadyTakenException, DataAccessException {

        var service = new UserService();
        var registerRequest = new RegisterRequest("user", "pass", "user@example.com");
        var registerResult = service.register(registerRequest);
        String badAuthToken = "bad4321auth1234";

        assertNotEquals(badAuthToken, registerResult.authToken());
        assertThrows(UnauthorizedException.class, () -> service.logout(badAuthToken));
    }

    @Test
    void listGamesGood() throws BadRequestException, AlreadyTakenException, DataAccessException, UnauthorizedException {

        var userService = new UserService();
        var registerRequest = new RegisterRequest("user", "pass", "user@example.com");
        var registerResult = userService.register(registerRequest);

        var gameService = new GameService(userService.getAuthDAO());

        // Empty list of games before adding new games
        assertTrue(gameService.listGames(registerResult.authToken()).games().isEmpty());

        var createGameRequestA = new CreateGameRequest("Game A");
        var createGameRequestB = new CreateGameRequest("Game B");
        var createGameRequestC = new CreateGameRequest("Game C");
        var createGameResultA = gameService.createGame(registerResult.authToken(), createGameRequestA);
        var createGameResultB = gameService.createGame(registerResult.authToken(), createGameRequestB);
        var createGameResultC = gameService.createGame(registerResult.authToken(), createGameRequestC);

        var listGamesResult = gameService.listGames(registerResult.authToken());

        assertEquals(3, listGamesResult.games().size());
        for (GameDataTruncated gameData : listGamesResult.games()) {
            assertNull(gameData.whiteUsername());
            assertNull(gameData.blackUsername());
        }

        assertTrue(createGameResultA.gameID() > 0 &&
                            createGameResultA.gameID() != createGameResultB.gameID() &&
                            createGameResultA.gameID() != createGameResultC.gameID());

    }

    @Test
    void listGamesBadAuth() throws BadRequestException, AlreadyTakenException, DataAccessException {

        var userService = new UserService();
        var registerRequest = new RegisterRequest("user", "pass", "user@example.com");
        var registerResult = userService.register(registerRequest);
        String badAuthToken = "bad4321auth1234";

        var gameService = new GameService(userService.getAuthDAO());

        assertNotEquals(badAuthToken, registerResult.authToken());
        assertThrows(UnauthorizedException.class, () -> gameService.listGames(badAuthToken));
    }


    @Test
    void createGameGood() throws BadRequestException, AlreadyTakenException, DataAccessException, UnauthorizedException {

        var userService = new UserService();
        var registerRequest = new RegisterRequest("user", "pass", "user@example.com");
        var registerResult = userService.register(registerRequest);

        var gameService = new GameService(userService.getAuthDAO());
        var createGameRequest = new CreateGameRequest("testGame");
        var createGameResult = gameService.createGame(registerResult.authToken(),createGameRequest);

        assertTrue(createGameResult.gameID() > 0);

    }

    @Test
    void createGameBadAuth() throws BadRequestException, AlreadyTakenException, DataAccessException {

        var userService = new UserService();
        var registerRequest = new RegisterRequest("user", "pass", "user@example.com");
        var registerResult = userService.register(registerRequest);
        String badAuthToken = "bad4321auth1234";

        var gameService = new GameService(userService.getAuthDAO());
        var createGameRequest = new CreateGameRequest("testGame");

        assertNotEquals(badAuthToken, registerResult.authToken());
        assertThrows(UnauthorizedException.class, () -> gameService.createGame(badAuthToken,createGameRequest));


    }

}
