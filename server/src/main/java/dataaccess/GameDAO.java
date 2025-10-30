package dataaccess;

import datamodel.GameDataTruncated;
import model.GameData;

import java.util.List;

public interface GameDAO {
    void clear() throws DataAccessException;
    int createGame(String gameName) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    void updateGame(int gameID, String username, String playerColor) throws DataAccessException;
    List<GameDataTruncated> listGames() throws DataAccessException;

}
