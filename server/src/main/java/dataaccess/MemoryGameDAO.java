package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.List;
import java.util.TreeMap;

public class MemoryGameDAO implements GameDAO{

    private final TreeMap<Integer, GameData> gameDataTreeMap = new TreeMap<>();

    @Override
    public void clear() {
        gameDataTreeMap.clear();
    }

    @Override
    public int createGame(String gameName) {
        int gameID;
        if (gameDataTreeMap.isEmpty()) {
            gameID = 1;
        } else {
            gameID = gameDataTreeMap.lastKey() + 1;
        }
        gameDataTreeMap.put(gameID, new GameData(gameID, "", "", gameName, new ChessGame()));
        return gameID;
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
