package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import datamodel.GameData;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLGameDAO implements GameDAO{

    public SQLGameDAO () throws DataAccessException {
        DatabaseManager.createDatabase();
        DatabaseManager.configureDatabase();
    }

    @Override
    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "TRUNCATE game_data";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: failed to clear game_data", e);
        }
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO game_data (gameName, game) VALUES (?,?)";
            try (PreparedStatement ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, gameName);

                String chessGameStr = new Gson().toJson(new ChessGame());
                ps.setString(2, chessGameStr);

                int rowsInserted = ps.executeUpdate();

                if (rowsInserted == 0) {
                    throw new DataAccessException("Error: no rows inserted");
                }

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: failed to insert game data");
        }
        return 0;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM game_data WHERE gameID=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGameData(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error: unable to retrieve auth data", e);
        }
        return null;
    }

    @Override
    public void updateGameUsers(int gameID, String username, ChessGame.TeamColor playerColor)
        throws DataAccessException {

        try (Connection conn = DatabaseManager.getConnection()) {

            String statement;
            if (playerColor.toString().equals("WHITE")) {
                statement = "UPDATE game_data SET whiteUsername = ? WHERE gameID = ?";
            } else {
                statement = "UPDATE game_data SET blackUsername = ? WHERE gameID = ?";
            }

            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                ps.setInt(2, gameID);

                int rowsUpdated = ps.executeUpdate();

                if (rowsUpdated == 0) {
                    throw new DataAccessException("Error: no rows updated");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: failed to update game data");
        }
    }

    @Override
    public void updateGame(int gameID, ChessGame game) throws DataAccessException {

        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "UPDATE game_data SET game = ? where gameID = ?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                String gameJson = new Gson().toJson(game);
                ps.setString(1, gameJson);
                ps.setInt(2, gameID);

                int rowsUpdated = ps.executeUpdate();

                if (rowsUpdated == 0) {
                    throw new DataAccessException("Error: no rows updated");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: failed to update game data");
        }
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {

        ArrayList<GameData> result = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM game_data";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readGameData(rs));
                    }
                    return result;
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error: unable to retrieve game data", e);
        }
    }

    private GameData readGameData(ResultSet rs) throws SQLException {

        var gameID = rs.getInt(1);
        var whiteUsername = rs.getString("whiteUsername");
        var blackUsername = rs.getString("blackUsername");
        var gameName = rs.getString("gameName");
        var game = new Gson().fromJson(rs.getString("game"), ChessGame.class);

        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }
}
