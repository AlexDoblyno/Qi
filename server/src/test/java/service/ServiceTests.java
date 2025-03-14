package service;

import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import model.JoinGameRequest;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import dataaccess.*;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;


public class ServiceTests {

    private AuthService authService;
    private GameService gameService;
    private UserService userService;
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private UserDAO userDAO;

    @BeforeEach
    public void setup() {
        this.userDAO = new MemoryUserDAO();
        this.authDAO = new MemoryAuthDAO();
        this.gameDAO = new MemoryGameDAO();
        authService = new AuthService(authDAO);
        gameService = new GameService(gameDAO, authDAO);
        userService = new UserService(userDAO, authDAO);

    }

    @Test
    public void testClearDataSuccess() throws DataAccessException {

        AuthData testAuth = new AuthData("1234", "testUser");

        gameService.createGame("SomeGame", testAuth);

        UserData user = new UserData("testUser", "password", "email@test.com");
        userService.register(user);

        gameService.clear();
        authService.clear();
        userService.clear();
        assertTrue(authDAO.getDBSize() == 0);
        assertThrows(DataAccessException.class, () -> gameService.listGames(testAuth));
        assertTrue(userDAO.getDBSize() == 0);

    }

    @Test
    public void registerUserSuccess() throws DataAccessException {
        UserData user = new UserData("testUser", "password", "email@test.com");
        AuthData authData = userService.register(user);
        assertNotNull(authData);
        assertEquals("testUser", authData.username());
    }

    @Test
    public void registerUserFail() throws DataAccessException {
        UserData user = new UserData("testUser", null, "email@test.com");
        assertThrows(DataAccessException.class, () -> userService.register(user));
    }

    @Test
    public void loginSuccess() throws DataAccessException {
        UserData user = new UserData("existingUser", "password", "email@test.com");
        userService.register(user);
        AuthData authData = userService.login(user);
        assertNotNull(authData);
    }

    @Test
    public void loginFail() throws DataAccessException {
        UserData user = new UserData("existingUser", "wrongPassword", "email@test.com");
        userService.register(new UserData("existingUser", "password", "email@test.com"));
        assertThrows(DataAccessException.class, () -> userService.login(user));
    }

    //NEEDS MORE WORK
    @Test
    public void logoutSuccess() throws DataAccessException {
        UserData user = new UserData("existingUser", "password", "email@test.com");
        AuthData auth = userService.register(user);
        userService.logout(auth);
        assertNull(authDAO.getAuth(auth));
    }

    @Test
    public void logoutFail() throws DataAccessException {
        UserData user = new UserData("existingUser", "password", "email@test.com");
        userService.register(user);
        AuthData auths = new AuthData("2134", "testStuff");
        assertThrows(DataAccessException.class, () -> userService.logout(auths));
    }

    @Test
    public void listGamesSuccess() throws DataAccessException {
        AuthData testAuth = new AuthData("1234", "testUser");

        gameService.createGame("SomeGame", testAuth);
        gameService.createGame("AnotherGame", testAuth);

        UserData user = new UserData("existingUser", "password", "email@test.com");
        AuthData auth = userService.register(user);

        Collection<GameData> games = gameService.listGames(auth);
        assertEquals(2, games.size());
    }

    @Test
    public void listGamesFail() throws DataAccessException {
        AuthData testAuth = new AuthData("1234", "testUser");

        gameService.createGame("SomeGame", testAuth);
        gameService.createGame("AnotherGame", testAuth);

        assertThrows(DataAccessException.class, () -> gameService.listGames(testAuth));
    }

    @Test
    public void createGameSuccess() throws DataAccessException {
        AuthData testAuth = new AuthData("1234", "testUser");

        int gameID = gameService.createGame("SomeGame", testAuth);

        assertNotNull(gameDAO.getGame(gameID));
    }

    @Test
    public void createGameFail() throws DataAccessException {
        AuthData testAuth = new AuthData("1234", "testUser");

        assertThrows(DataAccessException.class, () -> gameService.createGame(null, testAuth));
    }

    @Test
    public void joinGameSuccess() throws DataAccessException {
        AuthData testAuth = new AuthData("1234", "testUser");
        int id = gameService.createGame("SomeGame", testAuth);

        UserData user = new UserData("existingUser", "password", "email@test.com");
        AuthData auth = userService.register(user);

        JoinGameRequest request = new JoinGameRequest(id, "existingUser", "BLACK");
        gameService.joinGame(request, auth);

        GameData gameData = gameDAO.getGame(id);
        assertEquals("existingUser", gameData.getBlackUsername());
    }

    @Test
    public void joinGameFail() throws DataAccessException {
        AuthData testAuth = new AuthData("1234", "testUser");
        int id = gameService.createGame("SomeGame", testAuth);

        JoinGameRequest request = new JoinGameRequest(id, "existingUser", "BLACK");

        assertThrows(DataAccessException.class, () -> gameService.joinGame(request, testAuth));
    }
}
