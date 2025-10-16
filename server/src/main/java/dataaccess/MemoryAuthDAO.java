package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO{

    // Storage in RAM
    private final HashMap<String, AuthData> authDataHashMap = new HashMap<>();

    @Override
    public void createAuth(AuthData authData) {
        authDataHashMap.put(authData.authToken(), authData);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return authDataHashMap.get(authToken);
    }

    @Override
    public void deleteAuth(AuthData authData) {

    }
}
