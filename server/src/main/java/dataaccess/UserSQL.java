package dataaccess;

import model.User;
import org.mindrot.jbcrypt.BCrypt;
import server.ServerException;


import java.sql.SQLException;

public class UserDataSQL extends DataAccess {

    public UserSQL() {
        super();

        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS user (
            `username` VARCHAR(24) NOT NULL,
            `password` VARCHAR(72) NOT NULL,
            `email` VARCHAR(36) NULL,
            PRIMARY KEY (`username`)
            );我这里暂时无法语音 我得找个地方 ok?
            我可以qq打字有问题我直接回答
            """
        };
        try {
            ConfigureDatabase.configureDatabase(createStatements);
        } catch (DataAccessException e) {
            throw new RuntimeException();
        }
    }

    public void addUser(User user) throws DataAccessException, ServerException {
        try (var conn = DatabaseManager.getConnection()) {

            if (userExists(user)) {
                throw new DataAccessException("Error: Username already exists");
            }

            var sql = "INSERT INTO user (username, password, email) VALUES (?, ?, ?);";

            try (var stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, user.getUsername());
                stmt.setString(2, BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
                stmt.setString(3, user.getEmail());
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new DataAccessException("Error: " + e.getMessage());
            }
        } catch (ServerException e) {
            throw e;
        }

        catch (Exception e) {
            e.printStackTrace();
            throw new DataAccessException("Unable to read data");
        }
    }

    public User getUser(String username) throws DataAccessException, ServerException {
        try (var conn = DatabaseManager.getConnection()) {

            var sql = "SELECT * FROM user WHERE username = ?;";
            try (var stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                var rs = stmt.executeQuery();
                if (rs.next()) {
                    return new User(rs.getString("username"), rs.getString("password"), rs.getString("email"));
                }
                return null;
            } catch (SQLException e) {
                e.printStackTrace();
                throw new DataAccessException("Error: " + e.getMessage());
            }
        } catch (Exception e) {
            throw new DataAccessException("Unable to read data");
        }
    }

    public boolean userExists(User user) throws DataAccessException, ServerException {
        try (var conn = DatabaseManager.getConnection()) {

            var sql = "SELECT * FROM user WHERE username = ?;";
            try (var stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, user.getUsername());
                var rs = stmt.executeQuery();
                return rs.next();
            } catch (Exception e) {
                throw new DataAccessException("Error: " + e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerException("Unable to read data", 500);
        }
    }

    public void clear() throws DataAccessException, ServerException {
        try (var conn = DatabaseManager.getConnection()) {

            var sql = "DELETE FROM user;";
            try (var stmt = conn.prepareStatement(sql)) {
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new DataAccessException("Error: " + e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerException("Unable to read data", 500);
        }
    }

    public void deleteUser(User user) throws DataAccessException, ServerException {
        try (var conn = DatabaseManager.getConnection()) {

            if (!userExists(user)) {
                throw new DataAccessException("Error: Username does not exist");
            }

            var sql = "DELETE FROM user WHERE username = ?;";
            try (var stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, user.getUsername());
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new DataAccessException("Error: " + e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerException("Unable to read data", 500);
        }
    }
}
