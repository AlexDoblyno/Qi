package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import service.GameService;

import java.sql.SQLException;
import java.util.ArrayList;

public class SqlGameDAO implements GameDAO {

    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = conn.prepareStatement("TRUNCATE TABLE Game");
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public int createGame(GameData game) throws DataAccessException {
        if (game == null) {
            throw new DataAccessException("Error: bad request");
        }
        try (var conn = DatabaseManager.getConnection()) {
            String stuff = "INSERT INTO Game (gameID, whiteUsername, blackUsername, gameName, game, gameOver) VALUES (?, ?, ?, ?, ?, ?)";
            var statement = conn.prepareStatement(stuff);
            statement.setInt(1, game.gameID());
            statement.setString(2, game.whiteUsername());
            statement.setString(3, game.blackUsername());
            statement.setString(4, game.gameName());
            var jState = new Gson().toJson(game.game());
            statement.setString(5, jState);
            statement.setBoolean(6, game.gameOver());
            statement.executeUpdate();
            return game.gameID();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public GameData getGame(int id) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM Game WHERE gameID=?";

            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, id);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int gameID = rs.getInt("gameID");
                        String whiteUsername = rs.getString("whiteUsername");
                        String blackUsername = rs.getString("blackUsername");
                        String gameName = rs.getString("gameName");
                        ChessGame from = new Gson().fromJson(rs.getString("game"), ChessGame.class);
                        boolean gameOver = rs.getBoolean("gameOver");
                        return new GameData(gameID, whiteUsername, blackUsername, gameName, from, gameOver);
                    } else {
                        throw new DataAccessException("Error: Game ID is not valid");
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public ArrayList<GameData> listGame() throws DataAccessException {
        ArrayList<GameData> games = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM Game";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int gameID = rs.getInt("gameID");
                        String whiteUsername = rs.getString("whiteUsername");
                        String blackUsername = rs.getString("blackUsername");
                        String gameName = rs.getString("gameName");
                        ChessGame game = new Gson().fromJson(rs.getString("game"), ChessGame.class);
                        boolean gameOver = rs.getBoolean("gameOver");
                        GameData id = new GameData(gameID, whiteUsername, blackUsername, gameName, game, gameOver);
                        games.add(id);
                    }
                    return games;
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void updateGame(GameData data) throws DataAccessException {
        if (data == null) {
            throw new DataAccessException("Error: bad request");
        }
        try (var conn = DatabaseManager.getConnection()) {
            var statement = conn.prepareStatement("DELETE FROM Game WHERE gameID = ?");
            statement.setString(1, Integer.toString(data.gameID()));
            statement.executeUpdate();
            createGame(data);
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
