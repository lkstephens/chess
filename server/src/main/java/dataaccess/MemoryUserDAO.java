package dataaccess;

import model.UserData;

import javax.xml.crypto.Data;
import java.util.HashMap;

public class MemoryUserDAO implements UserDAO{

    private final HashMap<String, UserData> userDataHashMap = new HashMap<>();

    @Override
    public void clear() throws DataAccessException {
        try {
            userDataHashMap.clear();
        } catch (Exception e) {
            throw new DataAccessException("Error: database access error (HashMap)");
        }
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        try {
            userDataHashMap.put(userData.username(), userData);
        } catch (Exception e) {
            throw new DataAccessException("Error: database access error (HashMap)");
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try {
            return userDataHashMap.get(username);
        } catch (Exception e) {
            throw new DataAccessException("Error: database access error (HashMap)");
        }
    }
}
