package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void clear() throws DataAccessException;
    void createAuth(AuthData authData);
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(AuthData authData);
}
