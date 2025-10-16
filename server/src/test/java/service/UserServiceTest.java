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
    void registerGood() {
        var registerRequest = new RegisterRequest("lando", "lando", "lando@java.com");
        var user = new UserData("lando", "lando", "lando@java.com");

        var service = new UserService();

        try {

            RegisterResult response = service.register(registerRequest);
            assertNotNull(response);
            assertEquals(response.username(), user.username());
            assertNotNull(response.authToken());
            assertEquals(String.class, response.authToken().getClass());

        } catch (DataAccessException | UserService.AlreadyTakenException | BadRequestException e) {
            System.out.println(e.getMessage());
        }

    }

    @Test
    void registerEmptyFields() {

        var service = new UserService();

        var registerRequest1 = new RegisterRequest("", "test", "testemail@gmail.com");
        var registerRequest2 = new RegisterRequest("test2", "", "testemail@gmail.com");
        var registerRequest3 = new RegisterRequest("test3", "test", "");

        assertThrows(BadRequestException.class, () -> {
            service.register(registerRequest1);
        });

        assertThrows(BadRequestException.class, () -> {
            service.register(registerRequest2);
        });

        assertThrows(BadRequestException.class, () -> {
            service.register(registerRequest3);
        });

    }

}
