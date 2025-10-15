package service;

import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import datamodel.RegisterRequest;
import datamodel.RegisterResult;
import model.UserData;

public class UserService {

    private UserDAO UserDAO;

    public UserService() {
        UserDAO = new MemoryUserDAO();
    }

    public RegisterResult register(RegisterRequest registerRequest) throws BadRequestException {
        String username = registerRequest.username();
        String password = registerRequest.password();
        String email = registerRequest.email();

        // See if the user is already in the database
        if (UserDAO.getUser(username) != null) {
            // throw AlreadyTakenException
        } else {
            UserData userData = new UserData(username, password, email);
            UserDAO.createUser(userData);
        }

        return new RegisterResult(registerRequest.username(), "myAuthToken");
    }
    // public LoginResult login(LoginRequest loginRequest) {}
    // public void logout(LogoutRequest logoutRequest) {}
}
