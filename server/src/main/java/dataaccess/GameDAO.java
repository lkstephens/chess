package dataaccess;

import chess.ChessGame;
import datamodel.GameData;

import java.util.List;

public interface GameDAO {
    void clear() throws DataAccessException;
    int createGame(String gameName) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    void updateGameUsers(int gameID, String username, String playerColor) throws DataAccessException;
    void updateGame(int gameID, ChessGame game) throws DataAccessException;
    List<GameData> listGames() throws DataAccessException;

}
