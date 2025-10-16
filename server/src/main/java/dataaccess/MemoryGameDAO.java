package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryGameDAO implements GameDAO{

    private final HashMap<String, UserData> gameDataHashMap = new HashMap<>();

    @Override
    public void clear() {
        gameDataHashMap.clear();
    }
}
