package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO{

    private final HashMap<String, AuthData> authDataHashMap = new HashMap<>();

    @Override
    public void clear() throws DataAccessException {
        try {
            authDataHashMap.clear();
        } catch (Exception e) {
            throw new DataAccessException("Error: database access error (HashMap)");
        }
    }

    @Override
    public void createAuth(AuthData authData) {
        authDataHashMap.put(authData.authToken(), authData);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try {
            return authDataHashMap.get(authToken);
        } catch (Exception e) {
            throw new DataAccessException("Error: database access error (HashMap)");
        }
    }

    @Override
    public void deleteAuth(AuthData authData) {
        authDataHashMap.remove(authData.authToken());
    }
}
