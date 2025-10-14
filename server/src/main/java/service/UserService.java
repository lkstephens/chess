package service;

import datamodel.RegisterRequest;
import datamodel.RegisterResult;

public class UserService {
    public RegisterResult register(RegisterRequest registerRequest) throws BadRequestException {
        String username = registerRequest.username();
        String password = registerRequest.password();
        String email = registerRequest.email();

        return new RegisterResult(registerRequest.username(), "myAuthToken");
    }
    // public LoginResult login(LoginRequest loginRequest) {}
    // public void logout(LogoutRequest logoutRequest) {}
}
