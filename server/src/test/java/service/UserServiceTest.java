package service;

import dataaccess.DataAccessException;
import datamodel.LoginRequest;
import datamodel.LoginResult;
import datamodel.RegisterRequest;
import datamodel.RegisterResult;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    @Test
    void clear() {

    }

    @Test
    void registerGood() throws BadRequestException, UserService.AlreadyTakenException, DataAccessException {
        var registerRequest = new RegisterRequest("lando", "lando", "lando@java.com");

        var service = new UserService();

        RegisterResult response = service.register(registerRequest);
        assertNotNull(response);
        assertEquals(response.username(), registerRequest.username());
        assertNotNull(response.authToken());
        assertEquals(String.class, response.authToken().getClass());

    }

    @Test
    void registerEmptyFields() {

        var service = new UserService();

        var registerRequest1 = new RegisterRequest("", "test", "testemail@gmail.com");
        var registerRequest2 = new RegisterRequest("test2", "", "testemail@gmail.com");
        var registerRequest3 = new RegisterRequest("test3", "test", "");

        assertThrows(BadRequestException.class, () -> service.register(registerRequest1));

        assertThrows(BadRequestException.class, () -> service.register(registerRequest2));

        assertThrows(BadRequestException.class, () -> service.register(registerRequest3));

    }

    @Test
    void loginGood() throws BadRequestException, UserService.AlreadyTakenException, DataAccessException, UnauthorizedException {

        var registerRequest = new RegisterRequest("user", "pass", "user@example.com");
        var loginRequest = new LoginRequest("user", "pass");

        var service = new UserService();

        service.register(registerRequest);
        LoginResult response = service.login(loginRequest);

        assertNotNull(response);
        assertEquals(response.username(), loginRequest.username());
        assertEquals(response.username(),registerRequest.username());
        assertNotNull(response.authToken());
        assertEquals(String.class, response.authToken().getClass());

    }

    @Test
    void loginUserNotCreated() {

        var loginRequest = new LoginRequest("user", "pass");
        var service = new UserService();
        assertThrows(UnauthorizedException.class, () -> service.login(loginRequest));

    }

    @Test
    void loginIncorrectPassword() {

        var registerRequest = new RegisterRequest("user", "pass", "email@domain.com");
        var loginRequest = new LoginRequest("user", "password");
        var service = new UserService();

        assertThrows(UnauthorizedException.class, () -> {
            service.register(registerRequest);
            service.login(loginRequest);
        });

    }

    @Test
    void logoutGood() throws BadRequestException, UserService.AlreadyTakenException, DataAccessException, UnauthorizedException {

        var service = new UserService();

        // Register
        var registerRequest = new RegisterRequest("user", "pass", "user@example.com");

        // Logout
        var registerResult = service.register(registerRequest);
        assertEquals(String.class, registerResult.authToken().getClass());
        service.logout(registerResult.authToken());

        // Login
        var loginRequest = new LoginRequest("user", "pass");
        var loginResult = service.login(loginRequest);

        // Logout
        service.logout(loginResult.authToken());

    }

    @Test
    void logoutBadAuth() throws BadRequestException, UserService.AlreadyTakenException, DataAccessException {

        var service = new UserService();

        var registerRequest = new RegisterRequest("user", "pass", "user@example.com");
        var registerResult = service.register(registerRequest);
        String badAuthToken = "bad4321auth1234";

        assertNotEquals(badAuthToken, registerResult.authToken());
        assertThrows(UnauthorizedException.class, () -> service.logout(badAuthToken));
    }

}
