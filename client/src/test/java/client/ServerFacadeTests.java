package client;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.SqlGameDAO;
import exception.ResponseException;
import model.*;
import serverfacade.ServerFacade;
import org.junit.jupiter.api.*;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {
    public ServerFacade serverFacade;

    private GameDAO gameDAO;
    private static Server server;

    private static int port;

    @BeforeAll
    public static void init() {
        server = new Server();
        port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void setup() throws ResponseException {
        var serverUrl = "http://localhost:" + port;
        this.serverFacade = new ServerFacade(serverUrl);
        GameData gameData;
        AuthData authData;
        UserData userData;
        serverFacade.clear();

        this.gameDAO = new SqlGameDAO();

    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    public void clearAll() throws ResponseException {
        UserData testUser = new UserData("testUser", "testPassword", "testEmail");
        AuthData testAuth = serverFacade.registerUser(testUser);
        ChessGame game = new ChessGame();

        GameData testData = new GameData(5, null, null, "testGame", game, false);
        serverFacade.createGame(testData, testAuth);

        serverFacade.clear();

        UserData anotherTestUser = new UserData("testUser", "testPassword", "testEmail");
        AuthData anotherTestAuth = serverFacade.registerUser(anotherTestUser);
        GameList list = serverFacade.listGames(anotherTestAuth);
        assert (list.toString().equals("GameList[games=[]]"));
    }

    @Test
    public void registerUserSuccess() throws ResponseException {
        UserData testUser = new UserData("testUser", "testPassword", "testEmail");
        AuthData testAuth = serverFacade.registerUser(testUser);

        assertNotNull(testAuth);
    }

    @Test
    public void registerUserFail() {
        UserData testUser = new UserData(null, null, null);

        assertThrows(ResponseException.class, () -> serverFacade.registerUser(testUser));
    }

    @Test
    public void loginUserSuccess() throws ResponseException {
        UserData testUser = new UserData("testUser", "testPassword", "testEmail");
        serverFacade.registerUser(testUser);

        AuthData testAuth = serverFacade.loginUser(testUser);
        assertNotNull(testAuth);
    }

    @Test
    public void loginUserFail() throws ResponseException {
        UserData testUser = new UserData("testUser", "testPassword", "testEmail");
        serverFacade.registerUser(testUser);
        AuthData failUser = new AuthData(null, "testPassword");

        assertThrows(ResponseException.class, () -> serverFacade.logoutUser(failUser));
    }

    @Test
    public void logoutUserSuccess() throws ResponseException {
        UserData testUser = new UserData("testUser", "testPassword", "testEmail");
        AuthData testAuth = serverFacade.registerUser(testUser);

        serverFacade.logoutUser(testAuth);
        assertThrows(ResponseException.class, () -> serverFacade.listGames(testAuth));
    }

    @Test
    public void logoutUserFail() throws ResponseException {
        UserData testUser = new UserData("testUser", "testPassword", "testEmail");
        AuthData testAuth = serverFacade.registerUser(testUser);

        AuthData failAuth = new AuthData(null, null);

        assertThrows(ResponseException.class, () -> serverFacade.logoutUser(failAuth));
    }

    @Test
    public void listGamesSuccess() throws ResponseException {
        UserData testUser = new UserData("testUser", "testPassword", "testEmail");
        AuthData testAuth = serverFacade.registerUser(testUser);

        ChessGame game = new ChessGame();
        GameData testData = new GameData(5, null, null, "testGame", game, false);
        serverFacade.createGame(testData, testAuth);

        assertNotNull(serverFacade.listGames(testAuth));
    }

    @Test
    public void listGamesFail() throws ResponseException {
        UserData testUser = new UserData("testUser", "testPassword", "testEmail");
        AuthData testAuth = serverFacade.registerUser(testUser);

        ChessGame game = new ChessGame();
        GameData testData = new GameData(5, null, null, "testGame", game, false);
        serverFacade.createGame(testData, testAuth);

        AuthData failAuth = new AuthData(null, null);

        assertThrows(ResponseException.class, () -> serverFacade.listGames(failAuth));
    }

    @Test
    public void createGameSuccess() throws ResponseException {
        UserData testUser = new UserData("testUser", "testPassword", "testEmail");
        AuthData testAuth = serverFacade.registerUser(testUser);

        ChessGame game = new ChessGame();
        GameData testData = new GameData(5, null, null, "testGame", game, false);

        assertNotNull(serverFacade.listGames(testAuth));
    }

    @Test
    public void createGameFail() throws ResponseException {
        UserData testUser = new UserData("testUser", "testPassword", "testEmail");
        AuthData testAuth = serverFacade.registerUser(testUser);
        ChessGame game = new ChessGame();
        GameData testData = new GameData(5, null, null, "testGame", game, false);

        AuthData failAuth = new AuthData(null, null);
        assertThrows(ResponseException.class, () -> serverFacade.createGame(testData, failAuth));
    }

    @Test
    public void joinGameSuccess() throws ResponseException, DataAccessException {
        UserData testUser = new UserData("testUser", "testPassword", "testEmail");
        AuthData testAuth = serverFacade.registerUser(testUser);

        GameData testData = new GameData(5, null, null, "testGame", null, false);

        int id = gameDAO.createGame(testData);
        JoinGameRequest join = new JoinGameRequest(id, testUser.username(), "WHITE");

        serverFacade.joinGame(join, testAuth);

        assertNotNull(serverFacade.listGames(testAuth));

    }

    @Test
    public void joinGameFail() throws ResponseException, DataAccessException {
        UserData testUser = new UserData("testUser", "testPassword", "testEmail");
        AuthData testAuth = serverFacade.registerUser(testUser);

        ChessGame game = new ChessGame();
        GameData testData = new GameData(0, null, null, "testGame", game, false);

        int id = gameDAO.createGame(testData);

        JoinGameRequest join = new JoinGameRequest(id, "testUser", "WHITE");
        serverFacade.joinGame(join, testAuth);

        assertThrows(ResponseException.class, () -> serverFacade.joinGame(join, testAuth));
    }

}
