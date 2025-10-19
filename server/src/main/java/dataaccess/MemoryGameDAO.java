package dataaccess;

import chess.ChessGame;
import datamodel.GameDataTruncated;
import model.GameData;

import java.util.ArrayList;
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
    public List<GameDataTruncated> listGames() {
        ArrayList<GameData> raw = new ArrayList<>(gameDataTreeMap.values());
        ArrayList<GameDataTruncated> filtered = new ArrayList<>();
        for (GameData data : raw) {
            filtered.add(new GameDataTruncated(data.gameID(), data.whiteUsername(),
                                               data.blackUsername(), data.gameName()));
        }
        return filtered;
    }
}
