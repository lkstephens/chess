package service;

import dataaccess.DataAccessException;
import datamodel.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {

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

    @Test
    void joinGameGood() throws BadRequestException, AlreadyTakenException, DataAccessException, UnauthorizedException {

        var userService = new UserService();
        var registerRequest1 = new RegisterRequest("user", "pass", "user@example.com");
        var registerResult1 = userService.register(registerRequest1);

        var gameService = new GameService(userService.getAuthDAO());
        var createGameRequest = new CreateGameRequest("testGame");
        var createGameResult = gameService.createGame(registerResult1.authToken(), createGameRequest);

        var joinGameRequest1 = new JoinGameRequest("WHITE", createGameResult.gameID());

        gameService.joinGame(registerResult1.authToken(), joinGameRequest1);

        var registerRequest2 = new RegisterRequest("user2", "pass2", "user2@example.com");
        var registerResult2 = userService.register(registerRequest2);
        var joinGameRequest2 = new JoinGameRequest("BLACK", createGameResult.gameID());

        gameService.joinGame(registerResult2.authToken(), joinGameRequest2);

        var listGamesResult = gameService.listGames(registerResult2.authToken());

        assertEquals("user", listGamesResult.games().getFirst().whiteUsername());
        assertEquals("user2", listGamesResult.games().getFirst().blackUsername());
    }

    @Test
    void joinGameAlreadyTaken() throws BadRequestException, AlreadyTakenException, DataAccessException, UnauthorizedException {
        var userService = new UserService();
        var registerRequest1 = new RegisterRequest("user", "pass", "user@example.com");
        var registerResult1 = userService.register(registerRequest1);

        var gameService = new GameService(userService.getAuthDAO());
        var createGameRequest = new CreateGameRequest("testGame");
        var createGameResult = gameService.createGame(registerResult1.authToken(), createGameRequest);

        var joinGameRequest1 = new JoinGameRequest("WHITE", createGameResult.gameID());

        // Assign player 1 to white
        gameService.joinGame(registerResult1.authToken(), joinGameRequest1);

        var registerRequest2 = new RegisterRequest("user2", "pass2", "user2@example.com");
        var registerResult2 = userService.register(registerRequest2);
        var joinGameRequest2 = new JoinGameRequest("WHITE", createGameResult.gameID());

        // Assert assigning player 2 to white throws AlreadyTakenException
        assertThrows(AlreadyTakenException.class, () -> gameService.joinGame(registerResult2.authToken(), joinGameRequest2));

        var joinGameRequest3 = new JoinGameRequest("BLACK", createGameResult.gameID());

        // Assign player 1 to black
        gameService.joinGame(registerResult1.authToken(), joinGameRequest3);

        var joinGameRequest4 = new JoinGameRequest("BLACK", createGameResult.gameID());

        // Assert assigning player 2 to black throws AlreadyTakenException
        assertThrows(AlreadyTakenException.class, () -> gameService.joinGame(registerResult2.authToken(), joinGameRequest4));
    }
}
