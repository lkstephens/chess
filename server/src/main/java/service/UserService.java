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

    public void clear() throws DataAccessException {
        try {
            userDAO.clear();
            authDAO.clear();
        } catch (DataAccessException e) {
            throw new DataAccessException("Error: database access error (HashMap)", e);
        }
    }

    public RegisterResult register(RegisterRequest registerRequest) throws BadRequestException, AlreadyTakenException, DataAccessException {

        String username = registerRequest.username();
        String password = registerRequest.password();
        String email = registerRequest.email();

        // Check if there is a Bad Request (Checks for null, empty, and correct email syntax)
        if (username == null || password == null || email == null ||
            username.isEmpty() || password.isEmpty() ||
            !email.contains(".")) {
            throw new BadRequestException("Error: bad request");
        }

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

    public LoginResult login(LoginRequest loginRequest) throws BadRequestException, UnauthorizedException, DataAccessException {

        String username = loginRequest.username();
        String password = loginRequest.password();

        // Check if there is a Bad Request (Checks for null, empty)
        if (username == null || password == null ||
            username.isEmpty() || password.isEmpty()) {
            throw new BadRequestException("Error: bad request");
        }

        try {
            // See if the user is in the database
            UserData userData = userDAO.getUser(username);
            if (userData == null) {
                throw new UnauthorizedException("Error: no user found");
            } else {
                // Check for correct password
                if (!password.equals(userData.password())) {
                    throw new UnauthorizedException("Error: unauthorized");
                } else {
                    // Create Auth Data
                    String authToken = generateToken();
                    AuthData authData = new AuthData(authToken, username);
                    authDAO.createAuth(authData);
                    return new LoginResult(username, authToken);
                }
            }

        } catch (DataAccessException e) {
            throw new DataAccessException("Error: database access error (HashMap)", e);
        }

    }

    public void logout(String authToken) throws UnauthorizedException, DataAccessException {

        try {

            AuthData authData = authDAO.getAuth(authToken);
            if (authData == null) {
                throw new UnauthorizedException("Error: unauthorized");
            } else {
                authDAO.deleteAuth(authData);
            }

        } catch (DataAccessException e) {
            throw new DataAccessException("Error: database access error (HashMap)", e);
        }
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    // Facilitates sharing of the authData between UserService and GameService
    public AuthDAO getAuthDAO() {
        return authDAO;
    }
}
