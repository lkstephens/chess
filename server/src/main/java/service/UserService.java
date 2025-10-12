package service;

public class UserService {
    public RegisterResult register(RegisterRequest registerRequest) {
        return new RegisterResult(registerRequest.username(), registerRequest.password());
    }
    // public LoginResult login(LoginRequest loginRequest) {}
    // public void logout(LogoutRequest logoutRequest) {}
}
