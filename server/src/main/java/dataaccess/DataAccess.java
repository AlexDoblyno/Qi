package dataaccess;

public interface DataAccess {

    String[] CREATE_STATEMENTS = {
            """
        CREATE TABLE IF NOT EXISTS auth (
          `id` INT NOT NULL AUTO_INCREMENT,
          `authToken` VARCHAR(256) NOT NULL,
          `username` VARCHAR(256) NOT NULL,
          PRIMARY KEY (`id`),
          UNIQUE (`authToken`)
        );
        """,
            """
        CREATE TABLE IF NOT EXISTS user (
          `id` INT NOT NULL AUTO_INCREMENT,
          `username` VARCHAR(256) NOT NULL,
          `password` VARCHAR(256) NOT NULL,
          `email` VARCHAR(256) NOT NULL,
          PRIMARY KEY (`id`),
          UNIQUE (`username`)
        );
        """,
            """
        CREATE TABLE IF NOT EXISTS game (
          `id` INT NOT NULL AUTO_INCREMENT,
          `gameID` INT NOT NULL UNIQUE,
          `gameJSON` TEXT NOT NULL,
          PRIMARY KEY (`id`),
          UNIQUE (`gameID`)
        );
        """
    };

    static void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : CREATE_STATEMENTS) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (Exception ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}