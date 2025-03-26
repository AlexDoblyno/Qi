package service;

import chess.ChessGame;
import dataaccess.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import service.ClearService;
import service.GameService;
import service.RegisterService;
import service.UserService;

public class UserServiceTests {

    static UserDAO userDAO;
    static AuthDAO authDAO;
    static GameDAO gameDAO;

    static ClearService clearService;
    static RegisterService registerService;
    static UserService userService;
    static GameService gameService;

    @BeforeAll
    public static void init() {
        userDAO = new SqlUserDAO();
        authDAO = new SqlAuthDAO();
        gameDAO = new SqlGameDAO();

        clearService = new ClearService(gameDAO, authDAO, userDAO);
        registerService = new RegisterService(userDAO, authDAO);
        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(userDAO, gameDAO, authDAO);
    }

    @BeforeEach
    public void clear() throws DataAccessException {
        clearService.clear();
    }

    @Test
    public void registerSuccess() throws Exception {
        registerService.createUser("Sam", "password", "me@gmail.com");

        Assertions.assertTrue(userDAO.verifyUser("Sam"));
    }

    @Test
    public void registerFail() throws Exception {
        RegisterService registerService = new RegisterService(new MemUserDAO(), new MemAuthDAO());
        registerService.createUser("Sam", "password", "me@gmail.com");
        try {
            registerService.createUser("Sam", "password1", "me2@gmail.com");
        } catch (Exception e) {
            Assertions.assertInstanceOf(DataAccessException.class, e);
        }
    }

    @Test
    public void loginSuccess() throws Exception {
        registerService.createUser("Sam", "password", "me@gmail.com");
        Assertions.assertTrue(userDAO.verifyUser("Sam"));
        Assertions.assertTrue(authDAO.verifyAuthToken(userService.loginUser("Sam", "password")));
    }

    @Test
    public void loginFail() throws Exception {
        try {
            userService.loginUser("Sam", "password");
        } catch (Exception e) {
            Assertions.assertInstanceOf(DataAccessException.class, e);
        }
    }

    @Test
    public void logoutSuccess() throws Exception {
        String token = registerService.createUser("Sam", "password", "me@gmail.com");
        userService.logoutUser(token);
        Assertions.assertFalse(authDAO.verifyAuthToken(token));
    }

    @Test
    public void logoutFail() throws Exception {
        try {
            userService.logoutUser("Sam");
        } catch (Exception e) {
            Assertions.assertInstanceOf(DataAccessException.class, e);
        }
    }

    @Test
    public void createGameSuccess() throws Exception {
        registerService.createUser("Sam", "password", "me@gmail.com");
        Integer gameId = gameService.createGame("newGame");
        Assertions.assertNotNull(gameDAO.getGame(gameId));
    }

    @Test
    public void createGameFail() throws Exception {
        try {
            Integer gameId = gameService.createGame("    ");
        } catch (Exception e) {
            Assertions.assertInstanceOf(IllegalArgumentException.class, e);
        }
    }

    @Test
    public void joinGameSuccess() throws Exception {
        String token = registerService.createUser("Sam", "password", "me@gmail.com");
        Integer gameId = gameService.createGame("newGame");
        gameService.joinGame(token, gameId, ChessGame.TeamColor.BLACK);
        Assertions.assertEquals(gameDAO.getGame(gameId).blackUsername(), "Sam");
    }

    @Test
    public void joinGameFail() throws Exception {
        String token = registerService.createUser("Sam", "password", "me@gmail.com");
        String token2 = registerService.createUser("Ben", "password1", "me1@gmail.com");
        Integer gameId = gameService.createGame("newGame");
        gameService.joinGame(token, gameId, ChessGame.TeamColor.BLACK);
        try {
            gameService.joinGame(token2, gameId, ChessGame.TeamColor.BLACK);
        } catch (Exception e) {
            Assertions.assertInstanceOf(IllegalAccessException.class, e);
        }
    }

    @Test
    public void clearServiceSuccess() throws Exception {
        String token = registerService.createUser("Sam", "password", "me@gmail.com");
        Integer gameId = gameService.createGame("newGame");
        clearService.clear();

        try {
            Assertions.assertNull(userDAO.getUser("Sam", "password"));
            Assertions.assertNull(gameDAO.getGame(gameId));
            Assertions.assertNull(authDAO.getUsername(token));
        } catch (Exception e) {
            Assertions.assertInstanceOf(NullPointerException.class, e);
        }
    }

    @Test
    public void clearServiceFail() throws Exception {
        clearService.clear();
        Assertions.assertTrue(true);
    }

    @Test
    public void listGamesSuccess() throws Exception {
        String token = registerService.createUser("Sam", "password", "me@gmail.com");
        gameService.createGame("newGame");
        Assertions.assertNotNull(gameService.listGames(token));
    }

    @Test
    public void listGamesFail() throws Exception {
        gameService.createGame("newGame");
        try {
            Assertions.assertNull(gameService.listGames(""));
        } catch (Exception e) {
            Assertions.assertInstanceOf(DataAccessException.class, e);
        }
    }

    @Test
    public void verifyAuthTokenSuccess() throws Exception {
        String token = registerService.createUser("Sam", "password", "me@gmail.com");
        try {
            gameService.verifyAuthToken(token);
        } catch (Exception e) {
            Assertions.fail();
        }
        Assertions.assertTrue(true);
    }

    @Test
    public void verifyAuthTokenFail() throws Exception {
        try {
            gameService.verifyAuthToken("token");
        } catch (Exception e) {
            Assertions.assertInstanceOf(DataAccessException.class, e);
        }
    }

}
