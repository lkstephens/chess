package dataaccess;

import chess.ChessGame.TeamColor;
import datamodel.GameData;

import java.util.List;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class GameDAOTests {

    private static GameDAO gameDAO;

    @BeforeAll
    public static void createDAO() {
        try {
            gameDAO = new SQLGameDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    public void clearDataBefore() throws DataAccessException {
        gameDAO.clear();
    }

    @AfterEach
    public void clearDataAfter() throws DataAccessException {
        gameDAO.clear();
    }

    @Test
    void clear() throws DataAccessException {

        var gameName = "game1"; // gameID = 1

        gameDAO.createGame(gameName);
        assertNotNull(gameDAO.getGame(1));

        gameDAO.clear();
        assertNull(gameDAO.getGame(1));

        // Can clear twice
        gameDAO.clear();

    }

    @Test
    void createGameValidInput() throws DataAccessException {

        var gameName = "game"; // gameID = 1

        assertNull(gameDAO.getGame(1));
        gameDAO.createGame(gameName);
        assertNotNull(gameDAO.getGame(1));

        // Add a second game with the same name, gameID = 2
        assertNull(gameDAO.getGame(2));
        gameDAO.createGame(gameName);
        assertNotNull(gameDAO.getGame(2));

    }

    @Test
    void createGameInvalidInput() {
        assertThrows(DataAccessException.class, () -> gameDAO.createGame(null));
    }

    @Test
    void getGameSuccess() throws DataAccessException {

        var gameName = "game"; // gameID = 1
        gameDAO.createGame(gameName);

        var gameData = assertDoesNotThrow(() -> gameDAO.getGame(1));

        assertNotNull(gameData);
        assertEquals(gameName, gameData.gameName());

    }

    @Test
    void getGameFailure() throws DataAccessException {

        var gameName = "game"; // gameID = 1
        gameDAO.createGame(gameName);

        assertNull(gameDAO.getGame(2)); // wrong ID

    }

    @Test
    void updateGameSuccess() throws DataAccessException {

        gameDAO.createGame("game"); // gameID = 1
        gameDAO.updateGameUsers(1, "John", TeamColor.WHITE);
        gameDAO.updateGameUsers(1, "Jane", TeamColor.BLACK);

        var gameData = gameDAO.getGame(1);

        assertEquals("John", gameData.whiteUsername());
        assertEquals("Jane", gameData.blackUsername());

        // Same user for both colors
        gameDAO.createGame("game"); // gameID = 2
        gameDAO.updateGameUsers(2, "John", TeamColor.WHITE);
        assertDoesNotThrow(() -> gameDAO.updateGameUsers(2, "John", TeamColor.BLACK));

        // Removing user from a color
        assertDoesNotThrow(() -> gameDAO.updateGameUsers(2, null, TeamColor.WHITE));

    }

    @Test
    void updateGameFailure() throws DataAccessException {

        gameDAO.createGame("game");
        assertDoesNotThrow(() -> gameDAO.updateGameUsers(1, "John", TeamColor.WHITE));
        assertThrows(DataAccessException.class,
                () -> gameDAO.updateGameUsers(2, "John", TeamColor.BLACK));
    }

    @Test
    void listGamesSuccess() throws DataAccessException {

        gameDAO.createGame("game1");
        gameDAO.createGame("game2");
        gameDAO.createGame("game3");

        List<GameData> listGames = gameDAO.listGames();
        assertEquals(3, listGames.size());
        assertEquals("game1", listGames.getFirst().gameName());
        assertEquals("game2", listGames.get(1).gameName());
        assertEquals("game3", listGames.get(2).gameName());

    }

    @Test
    void listGamesEmpty() throws DataAccessException {

        List<GameData> listGames = gameDAO.listGames();
        assertTrue(listGames.isEmpty());

    }

}
