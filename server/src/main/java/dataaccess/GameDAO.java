package dataaccess;

import model.GameData;

import java.util.List;

public interface GameDAO {
    void clear();
    int createGame(String gameName);
    GameData getGame(int gameID);
    void updateGame(GameData gameData);
    List<GameData> listGames();

}
