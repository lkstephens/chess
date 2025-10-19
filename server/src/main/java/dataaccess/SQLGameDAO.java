package dataaccess;

import model.GameData;

import java.util.List;

public class SQLGameDAO implements GameDAO{
    @Override
    public void clear() {

    }

    @Override
    public int createGame(String gameName) {
        return 0;
    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public void updateGame(GameData gameData) {

    }

    @Override
    public List<GameData> listGames() {
        return List.of();
    }
}
