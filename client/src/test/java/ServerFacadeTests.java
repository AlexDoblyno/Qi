package client;

import chess.ChessGame;
import help.ServerFacade;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.*;
import server.Server;
import java.util.Objects;
import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {
    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init(){
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("localhost", port);
    }

    @BeforeEach
    void clearData() throws Exception {
        serverFacade.clear();
    }

    @AfterAll
    static void stopServer() throws Exception {
        serverFacade.clear();
        server.stop();
    }

    @Test
    public void goodRegister() throws Exception {
        AuthData returnData = serverFacade.register("User1", "Pw1", "email@email.com");
        assert returnData != null;
    }

    @Test
    public void badRegister() throws Exception {
        AuthData returnData = serverFacade.register("User1", "Pw2", "email@email.com");
        assertThrows(Exception.class, () -> {serverFacade.register("User1", "Pw2", "email@email.com");});
    }

    @Test
    public void goodLogin() throws Exception {
        serverFacade.register("User1", "Pw1", "email@email.com");
        AuthData returnData = serverFacade.login("User1", "Pw1");
        assert returnData != null;
    }

    @Test
    public void badLogin() throws Exception {
        serverFacade.register("User1", "Pw1", "email@email.com");
        assertThrows(Exception.class, () -> {serverFacade.login("User1", "Pw2");});
    }

    @Test
    public void goodLogout() throws Exception {
        serverFacade.register("User1", "Pw1", "email@email.com");
        AuthData returnData = serverFacade.login("User1", "Pw1");
        assertDoesNotThrow(() -> {serverFacade.logout(returnData.authToken());});
    }

    @Test
    public void badLogout() throws Exception {
        assertThrows(Exception.class, () -> {serverFacade.logout("Pw2");});
    }

    @Test
    public void goodCreateGame() throws Exception {
        serverFacade.register("User1", "Pw1", "email@email.com");
        AuthData returnData = serverFacade.login("User1", "Pw1");
        GameData returnGame = serverFacade.createGame("New Game", returnData.authToken());
        assert returnGame != null;
    }

    @Test
    public void badCreateGame() throws Exception {
        serverFacade.register("User1", "Pw1", "email@email.com");
        AuthData returnData = serverFacade.login("User1", "Pw1");
        assertThrows(Exception.class, () -> {serverFacade.createGame(null, returnData.authToken());});
    }

    @Test
    public void goodJoinGame() throws Exception {
        serverFacade.register("User1", "Pw1", "email@email.com");
        AuthData returnData = serverFacade.login("User1", "Pw1");
        GameData createData = serverFacade.createGame("NewGame", returnData.authToken());
        GameData returnGame = serverFacade.joinGame(createData.gameID(), ChessGame.TeamColor.BLACK, returnData.authToken());
        assert Objects.equals(returnGame.gameName(), "NewGame");
        assert Objects.equals(returnGame.blackUsername(), returnData.username());
    }

    @Test
    public void badJoinGame() throws Exception {
        serverFacade.register("User1", "Pw1", "email@email.com");
        AuthData returnData1 = serverFacade.login("User1", "Pw1");
        serverFacade.register("User2", "Pw1", "email@email.com");
        AuthData returnData2 = serverFacade.login("User2", "Pw1");
        GameData createData = serverFacade.createGame("NewGame", returnData1.authToken());
        serverFacade.joinGame(createData.gameID(), ChessGame.TeamColor.BLACK, returnData1.authToken());
        assertThrows(Exception.class, () -> {serverFacade.joinGame(createData.gameID(), ChessGame.TeamColor.BLACK, returnData2.authToken());});
    }


    @Test
    public void listGames() throws Exception {
        serverFacade.register("User1", "Pw1", "email@email.com");
        AuthData returnData = serverFacade.login("User1", "Pw1");
        serverFacade.createGame("1", returnData.authToken());
        serverFacade.createGame("2", returnData.authToken());
        serverFacade.createGame("3", returnData.authToken());
        serverFacade.createGame("4", returnData.authToken());
        GameData[] games = serverFacade.listGames(returnData.authToken());
        assert games.length == 4;
    }

    @Test
    public void badListGames() throws Exception {
        assertThrows(Exception.class, () -> {serverFacade.listGames(null);});
    }
}