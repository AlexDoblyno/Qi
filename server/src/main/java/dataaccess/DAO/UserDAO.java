package dataaccess.DAO;

import dataaccess.exceptions.*;
import dataaccess.SQLParent;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;

public class UserDAO extends SQLParent {
    private final String[] createStatement =
            new String[]{"""
                    CREATE TABLE IF NOT EXISTS User
                    (
                      username VARCHAR(50) NOT NULL,
                        password VARCHAR(64) DEFAULT NULL,
                        email VARCHAR(256) DEFAULT NULL,
                        PRIMARY KEY (username)
                    );
            """
            };

    private final String tableName = "User";

    public UserDAO() {
        createTable(createStatement);
    }

    public UserData createUser(UserData user) throws DataAccessException {
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        UserData hashedUser = new UserData(user.username(), hashedPassword, user.email());

        String sql = "INSERT INTO " + tableName + " (username, password, email) VALUES (?, ?, ?)";

        updateQuery(sql, hashedUser.username(), hashedUser.password(), hashedUser.email());

        return hashedUser;
    }


    public UserData getUser(String username) throws DataAccessException {
        String sql = "SELECT * FROM " + tableName + " WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection()){
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String password = rs.getString("password");
                        String email = rs.getString("email");
                        return new UserData(username, password, email);
                    }
                }
            }
        }
        catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to modify database: %s", e.getMessage()));
        }
        return null;
    }

    public void clear() throws BadRequestException {
        clearTable(tableName);
    }
}