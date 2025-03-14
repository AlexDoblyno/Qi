package dataaccess.DAO;

import dataaccess.exceptions.BadRequestException;
import dataaccess.DataAccessException;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Collection;
import static org.junit.jupiter.api.Assertions.*;

class GameDAOTest {
    private GameDAO gameDAO;

    @BeforeEach
    void setUp() throws DataAccessException, BadRequestException {
        (gameDAO = new GameDAO()).clear();
    }

    @Test
    void createGame() throws DataAccessException {
        GameData game = gameDAO.createGame("Testing");
        assertNotNull(game);
    }

    @Test
    void createInvalidGame() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> gameDAO.createGame(null));
    }

    @Test
    void getGame() throws DataAccessException {
        GameData game = gameDAO.createGame("Testing");
        int gameID = game.gameID();
        GameData getGame = gameDAO.getGame(gameID);
        assertEquals(game, getGame);
    }

    @Test
    void readAllGames() throws DataAccessException {
        GameData game = gameDAO.createGame("testing1");
        GameData game2 = gameDAO.createGame("testing2");
        GameData game3 = gameDAO.createGame("testing3");
        Collection<GameData> games = gameDAO.readAllGames();
        assertEquals(3, games.size());
    }

    @Test
    void readAllGamesAfterBadInsert() throws DataAccessException {
        GameData game1 = gameDAO.createGame("testing1");
        GameData game2 = gameDAO.createGame("testing2");
        GameData game3 = null;
        Collection<GameData> readGames = gameDAO.readAllGames();
        assertTrue(readGames.contains(game1));
        assertTrue(readGames.contains(game2));
        assertFalse(readGames.contains(game3));
    }

    @Test
    void readEmptyGame() throws DataAccessException {
        assertTrue(gameDAO.readAllGames().isEmpty());
    }

    @Test
    void updateGame() throws DataAccessException {
        GameData gameOne = gameDAO.createGame("Testing");
        GameData gameTwo = new GameData(gameOne.gameID(), "Testing", "hello", "changed", gameOne.game());
        gameDAO.updateGame(gameTwo);
        assertEquals(gameTwo, gameDAO.getGame(gameOne.gameID()));
    }

    @Test
    void updateGameNullName() throws DataAccessException {
        GameData gameOne = gameDAO.createGame("Testing");
        GameData gameTwo = new GameData(gameOne.gameID(), "Testing", "hello", null, gameOne.game());
        assertThrows(DataAccessException.class, () -> gameDAO.updateGame(gameTwo));
    }

    @Test
    void clear() throws DataAccessException, BadRequestException {
        GameData gameOne = gameDAO.createGame("Clear Testing");
        gameDAO.clear();
        Collection<GameData> games = gameDAO.readAllGames();
        assertEquals(0, games.size());
    }
}