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

    @AfterEach
    public void clearUserData() throws DataAccessException {
        authDAO.clear();
    }

    @Test
    void clear() throws DataAccessException {

        var authData = new AuthData("token", "username");

        authDAO.createAuth(authData);
        assertNotNull(authDAO.getAuth(authData.authToken()));

        authDAO.clear();
        assertNull(authDAO.getAuth(authData.authToken()));

        // Can clear twice
        authDAO.clear();
    }

}
