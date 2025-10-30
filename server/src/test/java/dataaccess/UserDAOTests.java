package dataaccess;

import model.UserData;
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

    @AfterEach
    public void clearUserData() throws DataAccessException {
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
    void createUserInvalidInput() throws DataAccessException {

        var user1 = new UserData(null, "password", "email@email.com");
        var user2 = new UserData("username", null, "email@email.com");
        var user3 = new UserData("username", "password", null);

        assertThrows(DataAccessException.class, () -> userDAO.createUser(user1));
        assertThrows(DataAccessException.class, () -> userDAO.createUser(user2));
        assertThrows(DataAccessException.class, () -> userDAO.createUser(user3));
    }

    @Test
    void getUserExists() throws DataAccessException {

        var user = new UserData("username","password","email@email.com");
        userDAO.createUser(user);

        UserData userData = assertDoesNotThrow(() -> userDAO.getUser(user.username()));

        assertEquals(user.username(), userData.username());
        assertEquals(user.password(), userData.password());
        assertEquals(user.email(), userData.email());

    }

    @Test
    void getUserDNE() throws DataAccessException {

        var user = new UserData("username", "password", "email@email.com");
        userDAO.createUser(user);

        assertNull(userDAO.getUser("wrongUsername"));
    }

}
