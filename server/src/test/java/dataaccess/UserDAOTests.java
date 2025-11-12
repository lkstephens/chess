package dataaccess;

import datamodel.UserData;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTests {

    private static UserDAO userDAO;

    @BeforeAll
    public static void createDAO() {
        try {
            userDAO = new SQLUserDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    public void clearDataBefore() throws DataAccessException {
        userDAO.clear();
    }

    @AfterEach
    public void clearDataAfter() throws DataAccessException {
        userDAO.clear();
    }

    @Test
    void clear() throws DataAccessException {

        var user = new UserData("username","password","email@email.com");

        userDAO.createUser(user);
        assertNotNull(userDAO.getUser(user.username()));

        userDAO.clear();
        assertNull(userDAO.getUser(user.username()));

        // Can clear twice
        userDAO.clear();
    }

    @Test
    void createUserValidInput() throws DataAccessException {

        var user = new UserData("username","password","email@email.com");

        assertNull(userDAO.getUser(user.username()));
        userDAO.createUser(user);
        assertNotNull(userDAO.getUser(user.username()));
    }

    @Test
    void createUserInvalidInput() {

        var user1 = new UserData(null, "password", "email@email.com");
        var user2 = new UserData("username", null, "email@email.com");
        var user3 = new UserData("username", "password", null);

        assertThrows(DataAccessException.class, () -> userDAO.createUser(user1));
        assertThrows(DataAccessException.class, () -> userDAO.createUser(user2));
        assertThrows(DataAccessException.class, () -> userDAO.createUser(user3));
    }

    @Test
    void getUserSuccess() throws DataAccessException {

        var user = new UserData("username","password","email@email.com");
        userDAO.createUser(user);

        UserData userData = assertDoesNotThrow(() -> userDAO.getUser(user.username()));

        assertNotNull(userData);
        assertEquals(user.username(), userData.username());
        assertEquals(user.password(), userData.password());
        assertEquals(user.email(), userData.email());

    }

    @Test
    void getUserFailure() throws DataAccessException {

        var user = new UserData("username", "password", "email@email.com");
        userDAO.createUser(user);

        assertNull(userDAO.getUser("wrongUsername"));

    }

}
