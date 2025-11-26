package dataaccess;

import datamodel.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO{

    public SQLAuthDAO () throws DataAccessException {
        DatabaseManager.createDatabase();
        DatabaseManager.configureDatabase();
    }

    @Override
    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "TRUNCATE auth_data";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: failed to clear auth_data", e);
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
            throw new DataAccessException("Error: failed to insert auth data");
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM auth_data WHERE authToken=?";
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


//    public String getUsername(String authToken) throws DataAccessException {
//        try (Connection conn = DatabaseManager.getConnection()) {
//            var statement = "SELECT username FROM auth_data WHERE authToken=?";
//            try (PreparedStatement ps = conn.prepareStatement(statement)) {
//                ps.setString(1, authToken);
//                try (ResultSet rs = ps.executeQuery()) {
//                    if (rs.next()) {
//                        return rs.getString("username");
//                    }
//                }
//            }
//        } catch (Exception e) {
//            throw new DataAccessException("Error: unable to retrieve auth data", e);
//        }
//        return null;
//    }

    private AuthData readAuthData(ResultSet rs) throws SQLException {
        var authToken = rs.getString("authToken");
        var username = rs.getString("username");
        return new AuthData(authToken, username);
    }
}
