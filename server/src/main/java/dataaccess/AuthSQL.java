package dataaccess;

import model.AuthToken;
import server.ServerException;

import java.sql.SQLException;

public class AuthSQL {

    public AuthSQL() {
        super();

        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS auth (
            `authToken` VARCHAR(36) NOT NULL,
            `username` VARCHAR(24) NOT NULL,
            PRIMARY KEY (`authToken`)
            );
            """
        };
        try {
            ConfigureDatabase.configureDatabase(createStatements);
        } catch (DataAccessException e) {
            throw new RuntimeException();
        }
    }

    public void addAuthToken(AuthToken authToken) throws DataAccessException, ServerException {
        try (var conn = DatabaseManager.getConnection()) {

            if (authTokenExists(String.valueOf(authToken))) {
                throw new DataAccessException("Error: Authentication token already exists");
            }

            var sql = "INSERT INTO auth (authToken, username) VALUES (?, ?);";
            try (var stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, authToken.getAuthToken());
                stmt.setString(2, authToken.getUsername());
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new DataAccessException("authError: " + e.getMessage());
            }
        } catch (Exception e) {
            throw new ServerException("Unable to read data", 500);
        }
    }

    public boolean checkAuthToken(String authToken) throws DataAccessException, ServerException {
        if (!authTokenExists(authToken)) {
            throw new DataAccessException("Error: Authentication token does not exist");
        }
        return true;
    }

    public void clear() throws DataAccessException, ServerException {
        try (var conn = DatabaseManager.getConnection()) {

            var sql = "DELETE FROM auth;";
            try (var stmt = conn.prepareStatement(sql)) {
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        } catch (Exception e) {
            throw new ServerException("Unable to read data", 500);
        }
    }

    public void deleteAuthToken(String authToken) throws DataAccessException, ServerException {
        try (var conn = DatabaseManager.getConnection()) {

            if (!authTokenExists(authToken)) {
                throw new DataAccessException("Error: Authentication token does not exist");
            }

            var sql = "DELETE FROM auth WHERE authToken = ?;";
            try (var stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, authToken);
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new DataAccessException("authError: " + e.getMessage());
            }
        } catch (Exception e) {
            throw new ServerException("Unable to read data", 500);
        }
    }

    public String getUsername(String authToken) throws DataAccessException, ServerException {
        try (var conn = DatabaseManager.getConnection()) {

            if (!authTokenExists(authToken)) {
                throw new DataAccessException("Error: Authentication token does not exist");
            }

            var sql = "SELECT username FROM auth WHERE authToken = ?;";
            try (var stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, authToken);
                var rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getString("username");
                }
            } catch (SQLException e) {
                throw new DataAccessException("authError: " + e.getMessage());
            }
            return null;
        } catch (Exception e) {
            throw new ServerException("Unable to read data", 500);
        }
    }

    public boolean authTokenExists(String authToken) throws DataAccessException, ServerException {
        try (var conn = DatabaseManager.getConnection()) {

            var sql = "SELECT * FROM auth WHERE authToken = ?;";
            try (var stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, authToken);
                var rs = stmt.executeQuery();
                return rs.next();
            } catch (SQLException e) {
                throw new DataAccessException("authError: " + e.getMessage());
            }
        } catch (Exception e) {
            throw new ServerException("Unable to read data", 500);
        }
    }
}
