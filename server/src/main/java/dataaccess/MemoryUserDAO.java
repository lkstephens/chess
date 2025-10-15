package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO{

    private HashMap<String, UserData> userDataHashMap = new HashMap<>();

    @Override
    public void createUser(UserData userData) {
        userDataHashMap.put(userData.username(), userData);
    }

    @Override
    public UserData getUser(String username) {
        return userDataHashMap.get(username);
    }
}
