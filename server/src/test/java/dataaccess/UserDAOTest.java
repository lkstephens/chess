package dataaccess;

import model.UserData;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTest {

    @Test
    void clear() throws DataAccessException {
        var user = new UserData("joe","j@j","j");
        UserDAO da = new MemoryUserDAO();
        da.createUser(user);
        assertNotNull(da.getUser(user.username()));
        //da.clear();
        assertNull(da.getUser(user.username()));
        //da.clear();
    }

}
