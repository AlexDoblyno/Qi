package dataaccess;

import model.AuthData;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class SqlAuthDAO implements AuthDAO {

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = conn.prepareStatement("TRUNCATE Auth");
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        if (username == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        try (var conn = DatabaseManager.getConnection()) {
            String token = generateToken();
            var statement = conn.prepareStatement("INSERT INTO Auth (authToken, username) VALUES (?, ?)");
            statement.setString(1, token);
            statement.setString(2, username);
            statement.executeUpdate();
            return new AuthData(token, username);
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public AuthData getAuth(AuthData auth) throws DataAccessException {
        if (auth == null) {
            throw new DataAccessException("Error: bad request");
        }
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM Auth WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, auth.authToken());
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String authToken = rs.getString("authToken");
                        String username = rs.getString("username");
                        return new AuthData(authToken, username);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    @Override
    public String getUsername(AuthData auth) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username FROM Auth WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, auth.authToken());
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String username = rs.getString("username");
                        return username;
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    @Override
    public void deleteAuth(AuthData auth) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "DELETE FROM Auth WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, auth.authToken());
                ps.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }

    }

    @Override
    public int getDBSize() throws DataAccessException {
        ArrayList<String> db = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM Auth";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String authToken = rs.getString("authToken");
                        db.add(authToken);
                    }
                    return db.size();
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }


}
