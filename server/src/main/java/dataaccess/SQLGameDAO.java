package dataaccess;

import model.GameData;
import datamodel.GameDataTruncated;

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
    public void updateGame(int gameID, String username, String playerColor) {

    }

    @Override
    public List<GameDataTruncated> listGames() {
        return List.of();
    }
}
