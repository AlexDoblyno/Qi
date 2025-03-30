package dataaccess;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class SqlGameDAOTests {
    private GameDAO gameDao;  // Assuming you have a GameDao class
    private UserDAO userDao;

    @BeforeEach
    public void setUp() throws DataAccessException {
        gameDao = new SqlGameDAO();
        userDao = new SqlUserDAO();
        gameDao.clear();
        userDao.clear();
    }

    @Test
    public void testClearSuccess() throws Exception {
        gameDao.clear();
        Assertions.assertEquals(gameDao.getSize(), 0);
    }

    @Test
    public void testCreateGameSuccess() throws Exception {
        Integer gameId = gameDao.createGame("Test Game"); // Mock success
        Assertions.assertEquals(1, gameId); // Assuming game ID returns 1
    }

    @Test
    public void testCreateGameFail() {
        Assertions.assertThrows(DataAccessException.class, () -> {
            gameDao.createGame("");
        });
    }

    @Test
    public void testGetGameSuccess() throws Exception {
        Integer gameId = gameDao.createGame("Test Game");
        GameData gameData = gameDao.getGame(gameId); // Mock success
        Assertions.assertNotNull(gameData);
        Assertions.assertEquals("Test Game", gameData.gameName());
    }

    @Test
    public void testGetGameFail() throws DataAccessException {
        Assertions.assertNull(gameDao.getGame(1));
    }

    @Test
    public void testListGamesSuccess() throws Exception {
        gameDao.createGame("Test Game");
        List<GameData> games = gameDao.listGames(); // Mock success
        Assertions.assertFalse(games.isEmpty());
        Assertions.assertEquals(1, games.size()); // Assuming 1 game returned
    }

    @Test
    public void testListGamesFail() throws DataAccessException {
        Assertions.assertTrue(gameDao.listGames().isEmpty());
    }

    @Test
    public void testUpdateGameSuccess() throws Exception {
        Integer gameId = gameDao.createGame("Test Game");
        userDao.createUser("user", "password", "email");
        gameDao.updateGame(ChessGame.TeamColor.WHITE, gameId, "user"); // Mock success
        Assertions.assertEquals("user", gameDao.getGame(gameId).whiteUsername());
    }

    @Test
    public void testUpdateGameFail() throws DataAccessException {
        Integer gameId = gameDao.createGame("Test Game");
        Assertions.assertThrows(DataAccessException.class, () -> {
            gameDao.updateGame(ChessGame.TeamColor.WHITE, gameId, "user");
        });
    }

    @Test
    public void testGetSizeSuccess() throws Exception {
        gameDao.createGame("Test Game");
        gameDao.createGame("Test Game2");
        Integer size = gameDao.getSize(); // Mock success
        Assertions.assertEquals(2, size); // Assuming size returns 1
    }

    @Test
    public void testGetSizeFail() throws DataAccessException {
        Assertions.assertEquals(0, gameDao.getSize());
    }

    @Test
    public void testVerifyGameSuccess() throws Exception {
        Integer gameId = gameDao.createGame("Test Game");
        Boolean exists = gameDao.verifyGame(gameId); // Mock success
        Assertions.assertTrue(exists);
    }

    @Test
    public void testVerifyGameFail() throws DataAccessException {
        Assertions.assertFalse(gameDao.verifyGame(1));
    }
}
