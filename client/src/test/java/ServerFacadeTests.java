package client;

import ResponseExceptionHandler.ResponseException;
import resultclasses.CreateGameResult;
import resultclasses.LoginResult;
import resultclasses.RegisterResult;
import org.junit.jupiter.api.*;
import server.bak.Server;
import serverfacade.ServerFacade;

public class ServerFacadeTests {

    private static ServerFacade serverFacade;
    private static Server server;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);

        serverFacade = new ServerFacade("http://localhost:" + port);

        System.out.println("Started test HTTP server on " + port);
    }

    @BeforeAll
    public static void setUp() throws ResponseException {
        serverFacade.clear();
    }

    @BeforeEach
    public void setUp2() throws ResponseException {
        serverFacade.clear();
        serverFacade.register("test", "test", "test");
    }

    @Test
    public void testRegisterPositive() {
        try {
            RegisterResult registerResult = serverFacade.register("username", "password", "email");
            Assertions.assertNotNull(registerResult);
            Assertions.assertNotNull(registerResult.getAuthToken());
        } catch (ResponseException e) {
            Assertions.fail();
        }
    }

    @Test
    public void testRegisterNegative() {
        try {
            RegisterResult registerResult = serverFacade.register("test", "test", "test");
            Assertions.fail();
        } catch (ResponseException e) {
            Assertions.assertEquals(500, e.statusCode());
        }
    }

    @Test
    public void testLoginPositive() {
        try {
            LoginResult loginResult = serverFacade.login("test", "test");
            Assertions.assertNotNull(loginResult);
            Assertions.assertNotNull(loginResult.getAuthToken());
        } catch (ResponseException e) {
            Assertions.fail();
        }
    }

    @Test
    public void testLoginNegative() {
        try {
            LoginResult loginResult = serverFacade.login("username", "wrongpassword");
            Assertions.fail();
        } catch (ResponseException e) {
            Assertions.assertEquals(500, e.statusCode());
        }
    }

    @Test
    public void testLogoutPositive() {
        try {
            LoginResult loginResult = serverFacade.login("test", "test");
            Assertions.assertNotNull(loginResult);
            Assertions.assertNotNull(loginResult.getAuthToken());
            serverFacade.logout(loginResult.getAuthToken());
        } catch (ResponseException e) {
            Assertions.fail();
        }
    }

    @Test
    public void testLogoutNegative() {
        try {
            LoginResult loginResult = serverFacade.login("test", "test");
            Assertions.assertNotNull(loginResult);
            Assertions.assertNotNull(loginResult.getAuthToken());
            serverFacade.logout(loginResult.getAuthToken());
            serverFacade.logout(loginResult.getAuthToken());
            Assertions.fail(); //use assert throws
        } catch (ResponseException e) {
            Assertions.assertEquals(500, e.statusCode());
        }
    }

    @Test
    public void testClear() {
        try {
            serverFacade.clear();
        } catch (ResponseException e) {
            Assertions.fail();
        }
    }

    @Test
    public void testListGamesPositive() {
        try {
            LoginResult loginResult = serverFacade.login("test", "test");
            Assertions.assertNotNull(loginResult);
            Assertions.assertNotNull(loginResult.getAuthToken());
            var res = serverFacade.listGames(loginResult.getAuthToken());
            Assertions.assertNotNull(res);
        } catch (ResponseException e) {
            Assertions.fail();
        }
    }

    @Test
    public void testListGamesNegative() {
        try {
            serverFacade.listGames(null);
            Assertions.fail();
        } catch (ResponseException e) {
            Assertions.assertEquals(500, e.statusCode());
        }
    }

    @Test
    public void testJoinGamePositive() throws ResponseException {
        LoginResult loginResult = serverFacade.login("test", "test");
        Assertions.assertNotNull(loginResult);
        Assertions.assertNotNull(loginResult.getAuthToken());
        CreateGameResult createGameResult = serverFacade.createGame(loginResult.getAuthToken(), "test");
        Assertions.assertNotNull(createGameResult);
        Assertions.assertNotNull(createGameResult.getGameID());
        var res = serverFacade.joinGame(loginResult.getAuthToken(), Integer.valueOf(createGameResult.getGameID()), "WHITE");
        Assertions.assertNotNull(res);
    }

    @Test
    public void testJoinGameNegative() {
        try {
            LoginResult loginResult = serverFacade.login("test", "test");
            Assertions.assertNotNull(loginResult);
            Assertions.assertNotNull(loginResult.getAuthToken());
            serverFacade.joinGame(loginResult.getAuthToken(), 0, "WHITE");
            serverFacade.joinGame(loginResult.getAuthToken(), 0, "WHITE");
            Assertions.fail();
        } catch (ResponseException e) {
            Assertions.assertEquals(500, e.statusCode());
        }
    }

    @Test
    public void testCreateGamePositive() {
        try {
            LoginResult loginResult = serverFacade.login("test", "test");
            Assertions.assertNotNull(loginResult);
            Assertions.assertNotNull(loginResult.getAuthToken());
            serverFacade.createGame(loginResult.getAuthToken(), "test");
        } catch (ResponseException e) {
            Assertions.fail();
        }
    }

    @Test
    public void testCreateGameNegative() {
        try {
            LoginResult loginResult = serverFacade.login("test", "test");
            serverFacade.createGame(loginResult.getAuthToken(), "test");
            Assertions.assertNotNull(loginResult);
        } catch (ResponseException e) {
            Assertions.assertEquals(500, e.statusCode());
        }
    }
}