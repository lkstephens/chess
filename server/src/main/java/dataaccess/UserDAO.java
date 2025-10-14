package dataaccess;

import datamodel.RegisterRequest;

public interface UserDAO {
    void createUser(RegisterRequest registerRequest);
    void getUser(String username);
}
