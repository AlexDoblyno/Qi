package dataaccess;

import model.AuthData;
import model.UserData;

import java.sql.SQLException;
import java.util.ArrayList;

public class SqlUserDAO implements UserDAO {

    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = conn.prepareStatement("TRUNCATE TABLE User");
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void createUser(UserData data) throws DataAccessException {
        if (data == null) {
            throw new DataAccessException("Error: bad request");
        }
        try (var conn = DatabaseManager.getConnection()) {
            var statement = conn.prepareStatement("INSERT INTO User (username, password, email) VALUES (?, ?, ?)");
            statement.setString(1, data.username());
            statement.setString(2, data.password());
            statement.setString(3, data.email());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        if (username == null) {
            throw new DataAccessException("Error: bad request");
        }
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM User WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String usernamer = rs.getString("username");
                        String password = rs.getString("password");
                        String email = rs.getString("email");
                        return new UserData(usernamer, password, email);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    @Override
    public int getDBSize() throws DataAccessException {
        ArrayList<String> db = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM User";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String username = rs.getString("username");
                        db.add(username);
                    }
                    return db.size();
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
