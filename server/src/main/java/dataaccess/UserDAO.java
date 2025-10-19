package dataaccess;

import model.UserData;

public interface UserDAO {
    void clear() throws DataAccessException;
    void createUser(UserData userData);
    UserData getUser(String username) throws DataAccessException;
}
