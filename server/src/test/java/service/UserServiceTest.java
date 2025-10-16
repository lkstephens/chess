package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryUserDAO;
import datamodel.RegisterRequest;
import datamodel.RegisterResult;
import model.UserData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    @Test
    void clear() {

    }

    @Test
    void register() throws UserService.AlreadyTakenException, DataAccessException {
        var registerRequest = new RegisterRequest("joe", "j@j", "j");
        var user = new UserData("joe", "j@j", "j");
        var authToken = "xyz";

        var da = new MemoryUserDAO();
        var service = new UserService();

        RegisterResult response = service.register(registerRequest);
        assertNotNull(response);
        assertEquals(response.username(), user.username());
        assertNotNull(response.authToken());
        assertEquals(String.class, response.authToken().getClass());
    }

}
