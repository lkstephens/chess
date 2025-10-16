package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import datamodel.RegisterRequest;
import datamodel.RegisterResult;
import model.UserData;

public class UserService {

    private UserDAO userDAO;

    public UserService() {
        userDAO = new MemoryUserDAO();
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

            // Create auth

            return new RegisterResult(username, "myAuthToken");

        } catch (DataAccessException e) {
            throw new DataAccessException("Error: database access error (HashMap)", e);
        }


    }
    // public LoginResult login(LoginRequest loginRequest) {}
    // public void logout(LogoutRequest logoutRequest) {}

    public static class AlreadyTakenException extends Exception {
        public AlreadyTakenException(String message) {
            super(message);
        }
    }
}
