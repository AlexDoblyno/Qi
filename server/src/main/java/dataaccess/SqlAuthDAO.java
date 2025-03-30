package dataaccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SqlAuthDAO implements AuthDAO {
    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM auth";
        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing auth table: " + e.getMessage());
        }
    }

    @Override
    public String getUsername(String authToken) throws DataAccessException {
        String sql = "SELECT username FROM auth WHERE authToken = ?";
        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, authToken);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("username");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error getting username from auth table: " + e.getMessage());
        }
        return null;
    }

    @Override
    public String createAuthToken(String username) throws DataAccessException {
        String sql = "INSERT INTO auth (username, authToken) VALUES (?, ?)";
        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            String authToken = UUID.randomUUID().toString();
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, authToken);

            preparedStatement.executeUpdate();
            return authToken;
        } catch (SQLException e) {
            throw new DataAccessException("Error creating auth token: " + e.getMessage());
        }
    }

    @Override
    public void deleteAuthToken(String authToken) throws DataAccessException {
        String sql = "DELETE FROM auth WHERE authToken = ?";
        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, authToken);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting auth token: " + e.getMessage());
        }
    }

    @Override
    public Integer getSize() throws DataAccessException {
        String sql = "SELECT COUNT(*) FROM auth";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error getting size from auth table: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public Boolean verifyAuthToken(String authToken) throws DataAccessException {
        String sql = "SELECT 1 FROM auth WHERE authToken = ? LIMIT 1";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, authToken);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return true;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error verifying auth token: " + e.getMessage());
        }
        return false;
    }
}
