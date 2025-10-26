package dataaccess;

import com.google.gson.Gson;
import model.AuthData;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO{

    public SQLAuthDAO () throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "TRUNCATE auth_data";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                int rowsDeleted = ps.executeUpdate();
                if (rowsDeleted == 0) {
                    throw new DataAccessException("Error: no rows deleted");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: failed to delete auth data");
        }
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException{
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO auth_data (authToken, username) VALUES (?,?)";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authData.authToken());
                ps.setString(2, authData.username());
                int rowsInserted = ps.executeUpdate();
                if (rowsInserted == 0) {
                    throw new DataAccessException("Error: no rows inserted");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: failed to delete auth data");
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM pet WHERE authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuthData(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error: unable to retrieve auth data", e);
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException{
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "DELETE FROM auth_data WHERE authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                int rowsDeleted = ps.executeUpdate();
                if (rowsDeleted == 0) {
                    throw new DataAccessException("Error: no rows deleted");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: failed to delete auth data");
        }
    }

//    private int executeUpdate(String statement, Object... params) throws DataAccessException, SQLException {
//        try (Connection conn = DatabaseManager.getConnection()) {
//            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
//                for (int i = 0; i < params.length; i++) {
//                    Object param = params[i];
//                    if (param instanceof String p) {
//                        ps.setString(i + 1, p);
//                    } else if (param == null) {
//                        ps.setNull(i + 1, NULL);
//                    }
//                }
//                ps.executeUpdate();
//
//                ResultSet rs = ps.getGeneratedKeys();
//                if (rs.next()) {
//                    return rs.getInt(1);
//                }
//
//                return 0;
//            }
//        } catch (SQLException e) {
//            throw new DataAccessException("Error: could not update database", e);
//        }
//    }

    private AuthData readAuthData(ResultSet rs) throws SQLException {
        var authToken = rs.getString("authToken");
        var username = rs.getString("username");
        return new AuthData(authToken, username);
    }

    private final String[] authCreateStatements = {
            """
            CREATE TABLE IF NOT EXISTS  auth_data (
              `authToken` char(36) NOT NULL,
              `username` varchar(32) NOT NULL,
              PRIMARY KEY (`authToken`)
              ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    // Creates table for AuthData
    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : authCreateStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error: could not configure database", ex);
        }
    }
}
