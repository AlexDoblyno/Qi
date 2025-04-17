package dataaccess;

import model.AuthData;

import java.sql.SQLException;

public interface AuthDAO extends DataAccess{

    static void clear() throws DataAccessException {
        var statement = "TURNCATE TABLE auth";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    static void createAuth(AuthData newAuth) throws DataAccessException {
        var statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, newAuth.getToken());
                preparedStatement.setString(2, newAuth.getUsername());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    static AuthData getAuth(String authToken) throws DataAccessException {
        var statement = "SELECT authToken, username FROM auth WHERE authToken=?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new AuthData(rs.getString("authToken"),
                                rs.getString("username"));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format(e.getMessage()));
        }
        return null;
    }

    static void deleteAuth(String authToken) throws DataAccessException {
        var statement = "DELETE FROM auth WHERE authToken=?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, authToken);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
