package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO{

    private final HashMap<String, UserData> userDataHashMap = new HashMap<>();

    @Override
    public void createUser(UserData userData) {
        userDataHashMap.put(userData.username(), userData);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException{
        try {
            return userDataHashMap.get(username);
        } catch (Exception e) {
            throw new DataAccessException("Error: database access error (HashMap)");
        }
    }
}
