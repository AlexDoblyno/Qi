package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.GameData;
import server.ServerException;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;

public class GameSQL extends DataAccess {

    public GameSQL() {
        super();

        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS auth (
            `gameID` INT AUTO_INCREMENT,
            `whiteUsername` VARCHAR(24) NULL,
            `blackUsername` VARCHAR(24) NULL,
            `spectators` text NULL,
            `gameName` VARCHAR(24) NULL,
            `game` text NULL,
            PRIMARY KEY (`gameID`)
            );
            """
        };
        try {
            ConfigureDatabase.configureDatabase(createStatements);
        } catch (DataAccessException e) {
            throw new RuntimeException();
        }
    }

    public boolean gameExists(Game game) throws DataAccessException, ServerException {
        try (var conn = DatabaseManager.getConnection()) {

            var sql = "SELECT * FROM game WHERE gameID = ?;";
            try (var stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, game.getGameID());
                var rs = stmt.executeQuery();
                return rs.next();
            } catch (Exception e) {
                throw new DataAccessException("Error: " + e.getMessage());
            }
        } catch (Exception e) {
            throw new ServerException("Unable to read data", 500);
        }
    }

    public int addGame(Game game) throws DataAccessException, ServerException {
        try (var conn = DatabaseManager.getConnection()) {

            if (gameExists(game)) {
                throw new DataAccessException("Error: GameID already exists");
            }

            var sql = "INSERT INTO game (whiteUsername, blackUsername, spectators, gameName, game) VALUES (?, ?, ?, ?, ?);";
            try (var stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                var gson = new Gson();

                stmt.setString(1, game.getWhiteUsername());
                stmt.setString(2, game.getBlackUsername());
                stmt.setString(3, gson.toJson(game.getSpectators()));
                stmt.setString(4, game.getGameName());
                stmt.setString(5, ChessGame.toJSON(game.getGame()));
                stmt.executeUpdate();

                var rs = stmt.getGeneratedKeys();

                if(rs.next()) {
                    return rs.getInt(1);
                }
            } catch (Exception e) {
                throw new DataAccessException("Error: " + e.getMessage());
            }
        } catch (Exception e) {
            throw new ServerException("Unable to read data", 500);
        }
        return 0;
    }

    public Game getGame(String gameID) throws DataAccessException, ServerException {
        try (var conn = DatabaseManager.getConnection()) {

            if (!gameExists(new Game(gameID, null, null, new HashSet<String>(), null, null))) {
                throw new DataAccessException("Error: GameID does not exist");
            }

            var sql = "SELECT * FROM game WHERE gameID = ?;";
            try (var stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, gameID);
                var rs = stmt.executeQuery();
                if (rs.next()) {
                    var whiteUsername = rs.getString("whiteUsername");
                    var blackUsername = rs.getString("blackUsername");
                    var gameName = rs.getString("gameName");
                    var spectatorsString = rs.getString("spectators");
                    HashSet<String> spectators = new Gson().fromJson(spectatorsString, new TypeToken<HashSet<String>>() {
                    }.getType());
                    var gameJSON = rs.getString("game");
                    var gson = new GsonBuilder()
                            .registerTypeAdapter(ChessGame.class, new ChessGame.ChessGameTA())
                            .create();
                    var game = gson.fromJson(gameJSON, ChessGame.class);
                    return new Game(gameID, whiteUsername, blackUsername, spectators, gameName, game);
                }
            } catch (Exception e) {
                throw new DataAccessException("Error: " + e.getMessage());
            }
            return null;
        } catch (Exception e) {
            throw new ServerException("Unable to read data", 500);
        }
    }

    public Game[] getAllGames() throws DataAccessException, ServerException {
        try (var conn = DatabaseManager.getConnection()) {

            var result = new ArrayList<Game>();

            var sql = "SELECT * FROM game;";
            try (var stmt = conn.prepareStatement(sql)) {
                var rs = stmt.executeQuery();
                while (rs.next()) {
                    var gameID = rs.getString("gameID");
                    var whiteUsername = rs.getString("whiteUsername");
                    var blackUsername = rs.getString("blackUsername");
                    var gameName = rs.getString("gameName");
                    var spectatorsString = rs.getString("spectators");
                    HashSet<String> spectators = new Gson().fromJson(spectatorsString, new TypeToken<HashSet<String>>() {
                    }.getType());
                    var gameJSON = rs.getString("game");
                    var gson = new GsonBuilder().registerTypeAdapter(ChessGame.class, new ChessGame.ChessGameTA()).create();
                    var game = gson.fromJson(gameJSON, ChessGame.class);

                    result.add(new Game(gameID, whiteUsername, blackUsername, spectators, gameName, game));
                }
            } catch (SQLException e) {
                throw new DataAccessException("Error: " + e.getMessage());
            }
            return result.toArray(new Game[0]);
        } catch (Exception e) {
            throw new ServerException("Unable to read data", 500);
        }
    }

    public Game updateGame(Game game) throws DataAccessException, ServerException {
        try (var conn = DatabaseManager.getConnection()) {

            if (!gameExists(game)) {
                throw new DataAccessException("Error: GameID does not exist");
            }

            var sql = "UPDATE game SET whiteUsername = ?, blackUsername = ?, spectators = ?, gameName = ?, game = ? WHERE gameID = ?;";
            try (var stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, game.getWhiteUsername());
                stmt.setString(2, game.getBlackUsername());
                stmt.setString(3, new Gson().toJson(game.getSpectators()));
                stmt.setString(4, game.getGameName());
                stmt.setString(5, ChessGame.toJSON(game.getGame()));
                stmt.setString(6, game.getGameID());
                stmt.executeUpdate();
            } catch (Exception e) {
                throw new DataAccessException("Error: " + e.getMessage());
            }

            return game;
        } catch (Exception e) {
            throw new ServerException("Unable to read data", 500);
        }
    }

    public void clear() throws ServerException {
        try (var conn = DatabaseManager.getConnection()) {
            String dropSql = "DROP TABLE IF EXISTS game;";
            try (var dropStmt = conn.prepareStatement(dropSql)) {
                dropStmt.executeUpdate();
            } catch (SQLException e) {
                throw new DataAccessException("Error dropping table: " + e.getMessage());
            }

            String createSql = "CREATE TABLE game (" +
                    "gameID INT AUTO_INCREMENT PRIMARY KEY," +
                    "whiteUsername VARCHAR(24)," +
                    "blackUsername VARCHAR(24)," +
                    "spectators TEXT," +
                    "gameName VARCHAR(24)," +
                    "game TEXT" +
                    ");";
            try (var createStmt = conn.prepareStatement(createSql)) {
                createStmt.executeUpdate();
            } catch (SQLException e) {
                throw new DataAccessException("Error creating table: " + e.getMessage());
            }
        } catch (Exception e) {
            throw new ServerException("Unable to clear data", 500);
        }
    }
}
