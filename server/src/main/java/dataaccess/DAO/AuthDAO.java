package dataaccess.DAO;

import dataaccess.DataAccessException;
import dataaccess.exceptions.*;
import dataaccess.DatabaseManager;
import dataaccess.SQLParent;
import model.AuthData;
import java.sql.*;

public class AuthDAO extends SQLParent {
    private final String[] createStatement =
            new String[]{"""
                    CREATE TABLE IF NOT EXISTS Authentication
                    (
                      authToken VARCHAR(256) NOT NULL,
                      username VARCHAR(50) NOT NULL,
                      PRIMARY KEY (authToken)
                    );
            """
            };

    private final String tableName = "Authentication";

    public AuthDAO() {
        createTable(createStatement);
    }


    public AuthData createAuth(String username) throws DataAccessException {
        AuthData newAuth = new AuthData(AuthData.createToken(), username);
        String updateStatement = "INSERT INTO Authentication (authToken, username) VALUES (?, ?)";

        updateQuery(updateStatement, newAuth.authToken(), username);

        return newAuth;
    }

    public AuthData getAuthByToken(String token) throws DataAccessException {
        String sql = "SELECT * FROM " + tableName + " WHERE authToken = ?";

        try (Connection conn = DatabaseManager.getConnection()){
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, token);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String username = rs.getString("username");
                        return new AuthData(token, username);
                    }
                }
            }
        }
        catch (SQLException | DataAccessException e) {
            throw new DataAccessException(String.format("Unable to modify database: %s", e.getMessage()));
        }
        return null;
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        String sql = "DELETE FROM " + tableName + " WHERE authToken = ?";

        try (Connection conn = DatabaseManager.getConnection()){
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, authToken);
                ps.executeUpdate();
            }
        }
        catch (SQLException | DataAccessException e) {
            throw new DataAccessException(String.format("Unable to modify database: %s", e.getMessage()));
        }
    }

    public void clear() throws BadRequestException {
        clearTable(tableName);
    }

    public void drop() throws BadRequestException {
        dropTable(tableName);
    }
}