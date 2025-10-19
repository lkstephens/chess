package dataaccess;

import datamodel.GameDataTruncated;
import model.GameData;

import java.util.List;

public interface GameDAO {
    void clear() throws DataAccessException;
    int createGame(String gameName);
    GameData getGame(int gameID);
    void updateGame(int gameID, String username, String playerColor);
    List<GameDataTruncated> listGames();

}
