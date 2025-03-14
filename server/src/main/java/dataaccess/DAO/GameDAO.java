package dataaccess.DAO;

import chess.ChessGame;
import dataaccess.exceptions.*;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.SQLParent;
import model.GameData;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import com.google.gson.Gson;

public class GameDAO extends SQLParent {
    private final String[] createStatement =
            new String[]{"""
                    CREATE TABLE IF NOT EXISTS Games
                    (
                        gameID SERIAL,
                        whiteUsername VARCHAR(256),
                        blackUsername VARCHAR(256),
                        gameName VARCHAR(256) NOT NULL,
                        chessGame JSON NOT NULL,
                        PRIMARY KEY (gameID)
                    );
            """
            };

    private final String tableName = "Games";

    public GameDAO(){
        createTable(createStatement);
    }

    public GameData createGame(String gameName) throws DataAccessException {

        ChessGame game = new ChessGame();
        int gameID = 0;

        Gson gson = new Gson();
        String gameJson = String.valueOf(gson.toJsonTree(game));

        String sql = "INSERT INTO " + tableName + "(whiteUsername, blackUsername, gameName, chessGame) VALUES (?,?,?,?)";

        try (Connection conn = DatabaseManager.getConnection()){
            try (PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, null);
                ps.setString(2, null);
                ps.setString(3, gameName);
                ps.setString(4, gameJson);
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int generatedId = rs.getInt(1);
                        gameID = generatedId;
                    }
                }
            }
        }
        catch (SQLException | DataAccessException e) {
            throw new DataAccessException(String.format("Unable to modify database: %s", e.getMessage()));
        }

        if (gameID == 0) {
            throw new DataAccessException(String.format("Unable to create game: %s", gameName));
        }

        GameData newGameData = new GameData(
                gameID,
                null,
                null,
                gameName,
                game
        );

        return newGameData;
    }

    public GameData getGame(int gameID) throws DataAccessException {
        String sql = "SELECT * FROM " + tableName + " WHERE gameID = ?";

        try (Connection conn = DatabaseManager.getConnection()){
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String whiteUsername = rs.getString("whiteUsername");
                        String blackUsername = rs.getString("blackUsername");
                        String gameName = rs.getString("gameName");
                        String chessGame = rs.getString("chessGame");
                        Gson gson = new Gson();
                        ChessGame game = gson.fromJson(chessGame, ChessGame.class);
                        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
                    }
                }
            }
        }
        catch (SQLException | DataAccessException e) {
            throw new DataAccessException(String.format("Unable to modify database: %s", e.getMessage()));
        }
        return null;
    }

    public Collection<GameData> readAllGames() throws DataAccessException {
        String sql = "SELECT gameID FROM " + tableName;
        ArrayList<GameData> games = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int gameID = rs.getInt("gameID");
                        GameData gameData = getGame(gameID);
                        if (gameData != null) {
                            games.add(gameData);
                        }
                    }
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException(String.format("Unable to read all games: %s", e.getMessage()));
        }

        return games;
    }

    public void updateGame(GameData game) throws DataAccessException {

        String sql = "UPDATE " + tableName + " SET whiteUsername = ?, blackUsername = ?, gameName = ?, chessGame = ? WHERE gameID = ?";
        Gson gson = new Gson();

        try (Connection conn = DatabaseManager.getConnection()){
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, game.whiteUsername());
                ps.setString(2, game.blackUsername());
                ps.setString(3, game.gameName());
                ps.setString(4, gson.toJson(game));
                ps.setInt(5, game.gameID());
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