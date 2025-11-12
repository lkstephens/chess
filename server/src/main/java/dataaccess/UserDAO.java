package dataaccess;

import datamodel.UserData;

public interface UserDAO {
    void clear() throws DataAccessException;
    void createUser(UserData userData) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
}
