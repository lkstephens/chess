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
    public void clear() throws DataAccessException {
        try {
            gameDataTreeMap.clear();
        } catch (Exception e) {
            throw new DataAccessException("Error: data access error (TreeMap)", e);
        }
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        try {
            int gameID;
            if (gameDataTreeMap.isEmpty()) {
                gameID = 1;
            } else {
                gameID = gameDataTreeMap.lastKey() + 1;
            }
            gameDataTreeMap.put(gameID, new GameData(gameID, null, null, gameName, new ChessGame()));
            return gameID;
        } catch (Exception e) {
            throw new DataAccessException("Error: data access error (TreeMap)", e);
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try {
            return gameDataTreeMap.get(gameID);
        } catch (Exception e) {
            throw new DataAccessException("Error: data access error (TreeMap)", e);
        }
    }

    @Override
    public void updateGame(int gameID, String username, String playerColor) throws DataAccessException {
        try {
            // Write over previous game data with a new record
            GameData oldGameData = gameDataTreeMap.get(gameID);
            GameData newGameData;
            if (playerColor.equals("WHITE")) {
                newGameData = new GameData(
                        gameID, username, oldGameData.blackUsername(), oldGameData.gameName(), oldGameData.game()
                );
            } else {
                newGameData = new GameData(
                        gameID, oldGameData.whiteUsername(), username, oldGameData.gameName(), oldGameData.game()
                );
            }
            gameDataTreeMap.put(gameID, newGameData);
        } catch (Exception e) {
            throw new DataAccessException("Error: data access error (TreeMap)", e);
        }
    }

    @Override
    public List<GameDataTruncated> listGames() throws DataAccessException {
        try {
            ArrayList<GameData> raw = new ArrayList<>(gameDataTreeMap.values());
            ArrayList<GameDataTruncated> filtered = new ArrayList<>();
            for (GameData data : raw) {
                filtered.add(new GameDataTruncated(data.gameID(), data.whiteUsername(),
                        data.blackUsername(), data.gameName()));
            }
            return filtered;

        } catch (Exception e) {
            throw new DataAccessException("Error: data access error (TreeMap)");
        }
    }
}
