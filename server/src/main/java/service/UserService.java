package service;

import dataaccess.*;
import datamodel.*;
import model.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

public class UserService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public void clear() throws DataAccessException {
        try {
            userDAO.clear();
            authDAO.clear();
        } catch (DataAccessException e) {
            throw new DataAccessException("Error: database access error", e);
        }
    }

    public RegisterResult register(RegisterRequest registerRequest) throws BadRequestException, AlreadyTakenException, DataAccessException {

        String username = registerRequest.username();
        String clearTextPassword = registerRequest.password();
        String email = registerRequest.email();

        // Check if there is a Bad Request (Checks for null, empty, and correct email syntax)
        if (username == null || clearTextPassword == null || email == null ||
            username.isEmpty() || clearTextPassword.isEmpty() ||
            !email.contains(".")) {
            throw new BadRequestException("Error: bad request");
        }

        String hashedPassword = BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());

        try {
            // See if the user is already in the database
            if (userDAO.getUser(username) != null) {
                throw new AlreadyTakenException("Error: already taken");
            } else {
                UserData userData = new UserData(username, hashedPassword, email);
                userDAO.createUser(userData);
            }

            // Create Auth Data
            String authToken = generateToken();
            AuthData authData = new AuthData(authToken, username);
            authDAO.createAuth(authData);

            return new RegisterResult(username, authToken);

        } catch (DataAccessException e) {
            throw new DataAccessException("Error: database access error", e);
        }
    }

    public LoginResult login(LoginRequest loginRequest) throws BadRequestException, UnauthorizedException, DataAccessException {

        String username = loginRequest.username();
        String clearTextPassword = loginRequest.password();

        // Check if there is a Bad Request (Checks for null, empty)
        if (username == null || clearTextPassword == null ||
            username.isEmpty() || clearTextPassword.isEmpty()) {
            throw new BadRequestException("Error: bad request");
        }

        try {
            // See if the user is in the database
            UserData userData = userDAO.getUser(username);

            if (userData == null) {
                throw new UnauthorizedException("Error: no user found");
            } else {
                // Check for correct password (hashed with BCrypt)
                if (!BCrypt.checkpw(clearTextPassword, userData.password())) {
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
            throw new DataAccessException("Error: database access error", e);
        }

    }

    public void logout(String authToken) throws UnauthorizedException, DataAccessException {

        try {

            AuthData authData = authDAO.getAuth(authToken);
            if (authData == null) {
                throw new UnauthorizedException("Error: unauthorized");
            } else {
                authDAO.deleteAuth(authToken);
            }

        } catch (DataAccessException e) {
            throw new DataAccessException("Error: database access error", e);
        }
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
