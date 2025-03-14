package dataaccess;

import dataaccess.exceptions.*;
import java.sql.*;

public class SQLParent {
    protected void createTable(String[] createTableStatement) {
        try {
            DatabaseManager.createDatabase();
        }
        catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        try (java.sql.Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createTableStatement) {
                try (java.sql.PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        }
        catch (SQLException | DataAccessException e) {
            try {
                throw new BadRequestException(String.format("Unable to configure database: %s", e.getMessage()));
            }
            catch (BadRequestException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    protected void clearTable(String table) throws BadRequestException {
        try (java.sql.Connection conn = DatabaseManager.getConnection()) {
            try (java.sql.PreparedStatement preparedStatement = conn.prepareStatement("DELETE FROM " + table)) {
                preparedStatement.executeUpdate();
            }
        }
        catch (SQLException | DataAccessException e) {
            throw new BadRequestException(String.format("Unable to configure database: %s", e.getMessage()));
        }
    }

    protected void dropTable(String table) throws BadRequestException {
        try (java.sql.Connection conn = DatabaseManager.getConnection()) {
            try (java.sql.PreparedStatement preparedStatement = conn.prepareStatement("DROP TABLE " + table)) {
                preparedStatement.executeUpdate();
            }
        }
        catch (SQLException | DataAccessException e) {
            throw new BadRequestException(String.format("Unable to configure database: %s", e.getMessage()));
        }
    }


    public int updateQuery(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {

            for (int i = 0; i < params.length; i++) {
                Object param = params[i];
                if (param == null) {
                    ps.setNull(i + 1, Types.NULL);
                } else {
                    switch (param) {
                        case String p -> ps.setString(i + 1, p);
                        case Integer p -> ps.setInt(i + 1, p);
                        case Double p -> ps.setDouble(i + 1, p);
                        case Long p -> ps.setLong(i + 1, p);
                        case Boolean p -> ps.setBoolean(i + 1, p);
                        default -> ps.setObject(i + 1, param);
                    }
                }
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    return 0;
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException(String.format("Unable to update database: %s", e.getMessage()));
        }
    }
}