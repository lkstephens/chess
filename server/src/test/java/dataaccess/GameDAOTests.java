package dataaccess;

import model.GameData;
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

    @AfterEach
    public void clearGameData() throws DataAccessException {
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

}
