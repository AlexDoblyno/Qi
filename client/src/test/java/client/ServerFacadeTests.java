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


    @Test
    public void sampleTest() {
        assertTrue(true);
    }
}
/// 需要改动和审阅
@Test
void register() throws Exception {
    var authData = facade.register("player1", "password", "p1@email.com");
    assertTrue(authData.authToken().length() > 10);
}
///回来赶紧看看，不要急 “然后，您可以直接使用如下单元测试示例中所示的测试来测试您的外观register。”