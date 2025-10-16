package service;

import dataaccess.*;
import datamodel.*;
import model.*;

import java.util.UUID;

public class UserService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
    }

    public RegisterResult register(RegisterRequest registerRequest) throws BadRequestException, AlreadyTakenException, DataAccessException {

        String username = registerRequest.username();
        String password = registerRequest.password();
        String email = registerRequest.email();

        // Check if there is a Bad Request (Checks for null, empty, and correct email syntax)
        if (username == null || password == null || email == null ||
            username.isEmpty() || password.isEmpty() ||
            !email.contains("@") || !email.contains(".")) {
            throw new BadRequestException("Error: bad request");
        }

        // Data access error handled with try-catch
        try {
            // See if the user is already in the database
            if (userDAO.getUser(username) != null) {
                throw new AlreadyTakenException("Error: already taken");
            } else {
                UserData userData = new UserData(username, password, email);
                userDAO.createUser(userData);
            }

            // Create Auth Data
            String authToken = generateToken();
            AuthData authData = new AuthData(authToken, username);
            authDAO.createAuth(authData);

            return new RegisterResult(username, authToken);

        } catch (DataAccessException e) {
            throw new DataAccessException("Error: database access error (HashMap)", e);
        }


    }
    // public LoginResult login(LoginRequest loginRequest) {}
    // public void logout(LogoutRequest logoutRequest) {}

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public static class AlreadyTakenException extends Exception {
        public AlreadyTakenException(String message) {
            super(message);
        }
    }
}
