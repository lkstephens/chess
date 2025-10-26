package dataaccess;

import model.AuthData;

import javax.xml.crypto.Data;
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
    public void createAuth(AuthData authData) throws DataAccessException {
        try {
            authDataHashMap.put(authData.authToken(), authData);
        } catch (Exception e) {
            throw new DataAccessException("Error: database access error (HashMap)");
        }
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
    public void deleteAuth(String authToken) throws DataAccessException {
        try {
            authDataHashMap.remove(authToken);
        } catch (Exception e) {
            throw new DataAccessException("Error: database access error (HashMap)");
        }
    }
}
