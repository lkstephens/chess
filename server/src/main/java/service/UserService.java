package service;

public class UserService {
    public RegisterResult register(RegisterRequest registerRequest) throws BadRequestException {
        String username = registerRequest.username();
        String password = registerRequest.password();
        String email = registerRequest.email();



        return new RegisterResult(registerRequest.username(), registerRequest.password());
    }
    // public LoginResult login(LoginRequest loginRequest) {}
    // public void logout(LogoutRequest logoutRequest) {}
}
