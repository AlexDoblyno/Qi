package client;

import chess.ChessGame;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dataaccess.DataAccessException;
import org.junit.jupiter.api.*;
import server.Server;
import client.*;
import ui.ServerFacade;


public class ServerFacadeTests {

    private static Server server;
    private ServerFacade serverFacade;
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
    public void setup() throws DataAccessException {
        serverFacade = new ServerFacade(Integer.toString(port));
        server.clear();
    }

    // Test for playGame with a successful response
    @Test
    public void testPlayGameSuccess() throws Exception {
        JsonObject jsonObject = JsonParser.parseString(serverFacade.registerUser("sam", "sam", "sam")).getAsJsonObject();
        String authToken = jsonObject.get("authToken").getAsString();
        serverFacade.createGame("new", authToken);
        Assertions.assertTrue(serverFacade.playGame("1", ChessGame.TeamColor.BLACK, authToken).isEmpty());
    }

    // Test for playGame with an invalid game ID
    @Test
    public void testPlayGameInvalidGameID() throws Exception {
        JsonObject jsonObject = JsonParser.parseString(serverFacade.registerUser("sam", "sam", "sam")).getAsJsonObject();
        String authToken = jsonObject.get("authToken").getAsString();
        Assertions.assertFalse(serverFacade.playGame("1", ChessGame.TeamColor.BLACK, authToken).isEmpty());
    }

    // Test for createGame with a successful response
    @Test
    public void testCreateGameSuccess() throws Exception {
        JsonObject jsonObject = JsonParser.parseString(serverFacade.registerUser("sam", "sam", "sam")).getAsJsonObject();
        String authToken = jsonObject.get("authToken").getAsString();
        Assertions.assertTrue(serverFacade.createGame("new", authToken).contains("gameID"));
    }

    // Test for createGame with an invalid auth token
    @Test
    public void testCreateGameInvalidAuthToken() throws Exception {
        Assertions.assertFalse(serverFacade.createGame("new", "authToken").contains("gameID"));
    }

    // Test for listGames with a successful response
    @Test
    public void testListGamesSuccess() throws Exception {
        JsonObject jsonObject = JsonParser.parseString(serverFacade.registerUser("sam", "sam", "sam")).getAsJsonObject();
        String authToken = jsonObject.get("authToken").getAsString();
        serverFacade.createGame("newGame", authToken);
        Assertions.assertTrue(serverFacade.listGames(authToken).contains("newGame"));
    }

    // Test for listGames with an invalid auth token
    @Test
    public void testListGamesInvalidAuthToken() throws Exception {
        serverFacade.createGame("newGame", "authToken");
        Assertions.assertFalse(serverFacade.listGames("authToken").contains("newGame"));
    }

    // Test for logout with a successful response
    @Test
    public void testLogoutSuccess() throws Exception {
        JsonObject jsonObject = JsonParser.parseString(serverFacade.registerUser("sam", "sam", "sam")).getAsJsonObject();
        String authToken = jsonObject.get("authToken").getAsString();
        Assertions.assertTrue(serverFacade.logout(authToken).isEmpty());
    }

    // Test for logout with an invalid auth token
    @Test
    public void testLogoutInvalidAuthToken() throws Exception {
        Assertions.assertFalse(serverFacade.logout("authToken").isEmpty());
    }

    // Test for registerUser with a successful response
    @Test
    public void testRegisterUserSuccess() throws Exception {
        Assertions.assertTrue(serverFacade.registerUser("sam", "sam", "sam").contains("authToken"));
    }

    // Test for registerUser with missing fields
    @Test
    public void testRegisterUserTwice() throws Exception {
        serverFacade.registerUser("sam", "sam", "sam");
        Assertions.assertFalse(serverFacade.registerUser("sam", "sam", "sam").contains("authToken"));
    }

    // Test for login with a successful response
    @Test
    public void testLoginSuccess() throws Exception {
        String authToken = serverFacade.registerUser("sam", "sam", "sam");
        serverFacade.logout(authToken);
        Assertions.assertTrue(serverFacade.login("sam", "sam").contains("authToken"));
    }

    // Test for login with invalid credentials
    @Test
    public void testLoginInvalidCredentials() throws Exception {
        Assertions.assertFalse(serverFacade.login("sam", "sam").contains("authToken"));
    }

}
