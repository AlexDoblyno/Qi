package service;

import dataaccess.exceptions.*;
import dataaccess.DataAccessException;
import dataaccess.DAO.GameDAO;
import dataaccess.DAO.MemoryAuthDAO;
import dataaccess.DAO.MemoryUserDAO;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;


class GameServiceTest {
    private MemoryAuthDAO memoryAuthDAO;
    private GameDAO memoryGameDAO;
    private MemoryUserDAO memoryUserDAO;
    private AuthData authData;
    private GameService gameService;

    @BeforeEach
    void setUp() {
        memoryAuthDAO = new MemoryAuthDAO();
        memoryGameDAO = new GameDAO();
        memoryUserDAO = new MemoryUserDAO();
        authData = memoryAuthDAO.createAuth("gameTester");
        gameService = new GameService(memoryGameDAO);
    }

    @Test
    void createGame() throws BadRequestException, DataAccessException {
        GameData testGame = new GameData(0, null, null, "Test", null);
        testGame = gameService.createGame(testGame);
        assertNotEquals(0, testGame.gameID());
    }

    @Test
    void createBadGame() throws BadRequestException {
        GameData testGame = new GameData(0, null, null, null, null);
        assertThrows(BadRequestException.class, () -> gameService.createGame(testGame));
    }

    @Test
    void listAllGamesNoGames() throws DataAccessException {
        Collection<GameData> test = gameService.listAllGames();
        assertEquals(0, test.size());
    }

    @Test
    void listAllGames2Games() throws DataAccessException, BadRequestException {
        GameData testGame = new GameData(0, null, null, "GameOne", null);
        GameData testGame2 = new GameData(0, null, null, "GameTwo", null);
        gameService.createGame(testGame);
        gameService.createGame(testGame2);
        Collection<GameData> test = gameService.listAllGames();
        assertEquals(2, test.size());
    }

    @Test
    void joinGameWhite() throws BadRequestException, UserExistsException, DataAccessException {
        GameData testGame = new GameData(0, null, null, "TestJoin", null);
        testGame = gameService.createGame(testGame);
        GameData checkJoin = gameService.joinGame(testGame.gameID(), "WHITE", authData.username());
        assertEquals(checkJoin.whiteUsername(), authData.username());
    }

    @Test
    void joinGameBlack() throws BadRequestException, UserExistsException, DataAccessException {
        GameData testGame = new GameData(0, null, null, "TestJoin", null);
        testGame = gameService.createGame(testGame);
        GameData checkJoin = gameService.joinGame(testGame.gameID(), "BLACK", authData.username());
        assertEquals(checkJoin.blackUsername(), authData.username());
    }

    @Test
    void invalidJoinGameWhite() throws BadRequestException, UserExistsException, DataAccessException {
        GameData testGame = new GameData(0, null, null, "TestJoin", null);
        testGame = gameService.createGame(testGame);
        testGame = gameService.joinGame(testGame.gameID(), "WHITE", "Bob");
        GameData finalTestGame = testGame;
        assertThrows(UserExistsException.class, () -> {gameService.joinGame(finalTestGame.gameID(), "WHITE", authData.username());});
    }

    @Test
    void clearGames() throws DataAccessException, BadRequestException {
        GameData testGame = new GameData(0, null, null, "Test", null);
        gameService.clearGames();
        assertTrue(gameService.listAllGames().isEmpty());
    }
}