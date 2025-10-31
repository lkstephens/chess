package dataaccess;

import model.AuthData;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class AuthDAOTests {

    private static AuthDAO authDAO;

    @BeforeAll
    public static void createDAO() {
        try {
            authDAO = new SQLAuthDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    public void clearDataBefore() throws DataAccessException {
        authDAO.clear();
    }

    @AfterEach
    public void clearDataAfter() throws DataAccessException {
        authDAO.clear();
    }

    @Test
    void clear() throws DataAccessException {

        var auth = new AuthData("token", "username");

        authDAO.createAuth(auth);
        assertNotNull(authDAO.getAuth(auth.authToken()));

        authDAO.clear();
        assertNull(authDAO.getAuth(auth.authToken()));

        // Can clear twice
        authDAO.clear();

    }

    @Test
    void createAuthValidInput() throws DataAccessException {

        var auth = new AuthData("token", "username");

        assertNull(authDAO.getAuth(auth.authToken()));
        authDAO.createAuth(auth);
        assertNotNull(authDAO.getAuth(auth.authToken()));

    }

    @Test
    void createAuthInvalidInput() {

        var auth1 = new AuthData(null, "username");
        var auth2 = new AuthData("token", null);

        assertThrows(DataAccessException.class, () -> authDAO.createAuth(auth1));
        assertThrows(DataAccessException.class, () -> authDAO.createAuth(auth2));

    }

    @Test
    void getAuthSuccess() throws DataAccessException {

        var auth = new AuthData("token", "username");
        authDAO.createAuth(auth);

        AuthData authData = assertDoesNotThrow(() -> authDAO.getAuth(auth.authToken()));

        assertNotNull(authData);
        assertEquals(auth.authToken(), authData.authToken());
        assertEquals(auth.username(), authData.username());

    }

    @Test
    void getAuthFailure() throws DataAccessException {

        var auth = new AuthData("token", "username");
        authDAO.createAuth(auth);

        assertNull(authDAO.getAuth("badToken"));

    }

    @Test
    void deleteAuthSuccess() throws DataAccessException {

        var auth = new AuthData("token", "username");
        authDAO.createAuth(auth);

        assertNotNull(authDAO.getAuth(auth.authToken()));

        authDAO.deleteAuth(auth.authToken());

        assertNull(authDAO.getAuth(auth.authToken()));

    }

    @Test
    void deleteAuthFailure() throws DataAccessException {

        var auth = new AuthData("token", "username");
        authDAO.createAuth(auth);

        assertThrows(DataAccessException.class, () -> authDAO.deleteAuth("badToken"));
    }

}
