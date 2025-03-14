package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.UserData;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static dataaccess.AuthDAO.*;
import static dataaccess.UserDAO.*;
import static dataaccess.GameDAO.*;

public class DatabaseUnitTests {

    @BeforeAll
    static void init() {
        try {
            DataAccess.configureDatabase();
        } catch (DataAccessException e) {
            System.err.println("Warning: Database initialization failed: " + e.getMessage());
        }
    }

    @BeforeEach
    void setUp() throws DataAccessException {
        AuthDAO.clear();
        UserDAO.clear();
        GameDAO.clear();
    }

    // AuthDAO

    @Test
    public void clearAuthSuccess() throws DataAccessException {
        // Positive
        createAuth(new AuthData("token", "bm888"));
        AuthDAO.clear();
        Assertions.assertNull(getAuth("token"));
    }

    @Test
    public void createAuthSuccess() throws DataAccessException {
        // positive
        createAuth(new AuthData("token", "bm888"));
        AuthData authData = getAuth("token");
        Assertions.assertNotNull(authData);
        Assertions.assertEquals("bm888", authData.username());
    }

    @Test
    public void createAuthFail() throws DataAccessException {
        // Negative
        createAuth(new AuthData("token", "bm888"));
        Assertions.assertThrows(DataAccessException.class, () ->
                createAuth(new AuthData("token", "bm888")));
    }

    @Test
    public void getAuthSuccess() throws DataAccessException {
        // positive
        createAuth(new AuthData("token", "bm888"));
        createAuth(new AuthData("token2", "bm"));
        AuthData authData = getAuth("token2");
        Assertions.assertNotNull(authData);
        Assertions.assertEquals("bm", authData.username());
    }

    @Test
    public void getAuthFail() throws DataAccessException {
        // negative
        createAuth(new AuthData("token", "bm888"));
        AuthData authData = getAuth("token2");
        Assertions.assertNull(authData);
    }

    @Test
    public void deleteAuthSuccess() throws DataAccessException {
        // Positive
        createAuth(new AuthData("token", "bm888"));
        AuthDAO.deleteAuth("token");
        Assertions.assertNull(getAuth("token"));
    }

    @Test
    public void deleteAuthFail() throws DataAccessException {
        // Positive
        createAuth(new AuthData("token", "bm888"));
        AuthDAO.deleteAuth("token2");
        Assertions.assertNotNull(getAuth("token"));
    }

    // UserDAO

    @Test
    public void clearUserSuccess() throws DataAccessException {
        // Positive
        createUser(new UserData("bm888", "brickwall", "bm888@byu.edu"));
        UserDAO.clear();
        Assertions.assertNull(getUser("bm888"));
    }

    @Test
    public void createUserSuccess() throws DataAccessException {
        // positive
        createUser(new UserData("bm888", "brickwall", "bm888@byu.edu"));
        UserData userData = getUser("bm888");
        Assertions.assertNotNull(userData);
        Assertions.assertEquals("brickwall", userData.password());
    }

    @Test
    public void createUserFail() throws DataAccessException {
        // Negative
        createUser(new UserData("bm888", "brickwall", "bm888@byu.edu"));
        Assertions.assertThrows(DataAccessException.class, () ->
                createUser(new UserData("bm888", "brickwall", "bm888@byu.edu")));
    }

    @Test
    public void getUserSuccess() throws DataAccessException {
        // positive
        createUser(new UserData("bm888", "brickwall", "bm888@byu.edu"));
        createUser(new UserData("bm", "brickwall", "bm888@byu.edu"));
        UserData userData = getUser("bm");
        Assertions.assertNotNull(userData);
        Assertions.assertEquals("brickwall", userData.password());
    }

    @Test
    public void getUserFail() throws DataAccessException {
        // negative
        createUser(new UserData("bm888", "brickwall", "bm888@byu.edu"));
        UserData userData = getUser("bm");
        Assertions.assertNull(userData);
    }

    // GameDAO

    @Test
    public void clearGameSuccess() throws DataAccessException {
        // Positive
        createGame(new GameData(1234,
                null, null, "game1", new ChessGame()));
        GameDAO.clear();
        Assertions.assertNull(getGame(1234));
    }

    @Test
    public void createGameSuccess() throws DataAccessException {
        // positive
        createGame(new GameData(1234,
                null, null, "game1", new ChessGame()));
        GameData gameData = getGame(1234);
        Assertions.assertNotNull(gameData);
        Assertions.assertEquals("game1", gameData.gameName());
    }

    @Test
    public void createGameFail() throws DataAccessException {
        // Negative
        createGame(new GameData(1234,
                null, null, "game1", new ChessGame()));
        Assertions.assertThrows(DataAccessException.class, () ->
                createGame(new GameData(1234,
                        null, null, "game1", new ChessGame())));
    }

    @Test
    public void getGameSuccess() throws DataAccessException {
        // positive
        createGame(new GameData(1234,
                null, null, "game1", new ChessGame()));
        createGame(new GameData(123,
                null, null, "game1", new ChessGame()));
        GameData gameData = getGame(123);
        Assertions.assertNotNull(gameData);
        Assertions.assertEquals("game1", gameData.gameName());
    }

    @Test
    public void getGameFail() throws DataAccessException {
        // negative
        createGame(new GameData(1234,
                null, null, "game1", new ChessGame()));
        GameData gameData = getGame(123);
        Assertions.assertNull(gameData);
    }

    @Test
    public void listGameSuccess() throws DataAccessException {
        createGame(new GameData(1234,
                null, null, "game1", new ChessGame()));
        createGame(new GameData(12345,
                null, null, "game2", new ChessGame()));
        ArrayList<GameData> games = (ArrayList<GameData>) listGames();
        Assertions.assertNotNull(games);
        Assertions.assertEquals("game1", games.get(0).gameName());
        Assertions.assertEquals("game2", games.get(1).gameName());
    }

    @Test
    public void listGameFail() throws DataAccessException {
        createGame(new GameData(1234,
                null, null, "game1", new ChessGame()));
        createGame(new GameData(12345,
                null, null, "game2", new ChessGame()));
        GameDAO.clear();
        ArrayList<GameData> games = (ArrayList<GameData>) listGames();
        Assertions.assertTrue(games.isEmpty());
    }

    @Test
    public void updateGameSuccess() throws DataAccessException {
        createGame(new GameData(1234,
                null, null, "game1", new ChessGame()));
        GameData gameData = getGame(1234);
        Assertions.assertNotNull(gameData);
        GameData newGame = new GameData(gameData.gameID(),
                "bm888", gameData.blackUsername(),
                gameData.gameName(), gameData.game());
        updateGame(newGame);
        GameData retrievedGame = getGame(1234);
        Assertions.assertNotNull(retrievedGame);
        Assertions.assertEquals("bm888", retrievedGame.whiteUsername());
    }

    @Test
    public void updateGameFail() throws DataAccessException {
        createGame(new GameData(1234,
                null, null, "game1", new ChessGame()));
        GameData gameData = getGame(1234);
        Assertions.assertNotNull(gameData);
        GameData newGame = new GameData(123,
                "bm888", gameData.blackUsername(),
                gameData.gameName(), gameData.game());
        updateGame(newGame);
        GameData retrievedGame = getGame(1234);
        Assertions.assertNotNull(retrievedGame);
        Assertions.assertNull(retrievedGame.whiteUsername());
    }
}
