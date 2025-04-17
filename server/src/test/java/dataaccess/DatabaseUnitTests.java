package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.User;
import model.Game;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;

import static dataaccess.AuthDAO.*;

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
        Assertions.assertEquals("bm888", authData.getUsername());
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
        Assertions.assertEquals("bm", authData.getUsername());
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
        createUser(new User("bm888", "brickwall", "bm888@byu.edu"));
        UserDAO.clear();
        Assertions.assertNull(getUser("bm888"));
    }

    @Test
    public void createUserSuccess() throws DataAccessException {
        // positive
        createUser(new User("bm888", "brickwall", "bm888@byu.edu"));
        User user = getUser("bm888");
        Assertions.assertNotNull(user);
        Assertions.assertEquals("brickwall", user.password());
    }

    @Test
    public void createUserFail() throws DataAccessException {
        // Negative
        createUser(new User("bm888", "brickwall", "bm888@byu.edu"));
        Assertions.assertThrows(DataAccessException.class, () ->
                createUser(new User("bm888", "brickwall", "bm888@byu.edu")));
    }

    @Test
    public void getUserSuccess() throws DataAccessException {
        // positive
        createUser(new User("bm888", "brickwall", "bm888@byu.edu"));
        createUser(new User("bm", "brickwall", "bm888@byu.edu"));
        User user = getUser("bm");
        Assertions.assertNotNull(user);
        Assertions.assertEquals("brickwall", user.password());
    }

    @Test
    public void getUserFail() throws DataAccessException {
        // negative
        createUser(new User("bm888", "brickwall", "bm888@byu.edu"));
        User user = getUser("bm");
        Assertions.assertNull(user);
    }

    // GameDAO

    @Test
    public void clearGameSuccess() throws DataAccessException {
        // Positive
        createGame(new Game(1234,
                null, null, new HashSet<>(), "game1", new ChessGame()));
        GameDAO.clear();
        Assertions.assertNull(getGame(1234));
    }

    @Test
    public void createGameSuccess() throws DataAccessException {
        // positive
        createGame(new Game(1234,
                null, null, new HashSet<>(), "game1", new ChessGame()));
        Game game = getGame(1234);
        Assertions.assertNotNull(game);
        Assertions.assertEquals("game1", game.getGameName());
    }

    @Test
    public void createGameFail() throws DataAccessException {
        // Negative
        createGame(new Game(1234,
                null, null, new HashSet<>(), "game1", new ChessGame()));
        Assertions.assertThrows(DataAccessException.class, () ->
                createGame(new Game(1234,
                        null, null, new HashSet<>(), "game1", new ChessGame())));
    }

    @Test
    public void getGameSuccess() throws DataAccessException {
        // positive
        createGame(new Game(1234,
                null, null, new HashSet<>(), "game1", new ChessGame()));
        createGame(new Game(123,
                null, null, new HashSet<>(), "game1", new ChessGame()));
        Game game = getGame(123);
        Assertions.assertNotNull(game);
        Assertions.assertEquals("game1", game.getGameName());
    }

    @Test
    public void getGameFail() throws DataAccessException {
        // negative
        createGame(new Game(1234,
                null, null, new HashSet<>(), "game1", new ChessGame()));
        Game game = getGame(123);
        Assertions.assertNull(game);
    }

    @Test
    public void listGameSuccess() throws DataAccessException {
        createGame(new Game(1234,
                null, null, new HashSet<>(), "game1", new ChessGame()));
        createGame(new Game(12345,
                null, null, new HashSet<>() ,"game2", new ChessGame()));
        ArrayList<Game> games = (ArrayList<Game>) listGames();
        Assertions.assertNotNull(games);
        Assertions.assertEquals("game1", games.get(0).getGameName());
        Assertions.assertEquals("game2", games.get(1).getGameName());
    }

    @Test
    public void listGameFail() throws DataAccessException {
        createGame(new Game(1234,
                null, null, new HashSet<>(), "game1", new ChessGame()));
        createGame(new Game(12345,
                null, null, new HashSet<>(), "game2", new ChessGame()));
        GameDAO.clear();
        ArrayList<Game> games = (ArrayList<Game>) listGames();
        Assertions.assertTrue(games.isEmpty());
    }

    @Test
    public void updateGameSuccess() throws DataAccessException {
        createGame(new Game(1234,
                null, null, new HashSet<>(), "game1", new ChessGame()));
        Game game = getGame(1234);
        Assertions.assertNotNull(game);
        Game newGame = new Game(game.getGameID(),
                "bm888", game.getBlackUsername(), game.getSpectators(),
                game.getGameName(), game.getGame());
        updateGame(newGame);
        Game retrievedGame = getGame(1234);
        Assertions.assertNotNull(retrievedGame);
        Assertions.assertEquals("bm888", retrievedGame.getWhiteUsername());
    }

    @Test
    public void updateGameFail() throws DataAccessException {
        createGame(new Game(1234,
                null, null, new HashSet<>(), "game1", new ChessGame()));
        Game game = getGame(1234);
        Assertions.assertNotNull(game);
        Game newGame = new Game(123,
                "bm888", game.getBlackUsername(), game.getSpectators(),
                game.getGameName(), game.getGame());
        updateGame(newGame);
        Game retrievedGame = getGame(1234);
        Assertions.assertNotNull(retrievedGame);
        Assertions.assertNull(retrievedGame.getWhiteUsername());
    }
}
