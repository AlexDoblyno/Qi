package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.*;
import org.junit.jupiter.api.*;

public class ServiceTests {

    private static ClearService clearService;
    private static GameService gameService;
    private static UserService userService;

    @BeforeAll
    static void init() {
        clearService = new ClearService();
        gameService = new GameService();
        userService = new UserService();

        try {
            DataAccess.configureDatabase();
        } catch (DataAccessException e) {
            System.err.println("Warning: Database initialization failed: " + e.getMessage());
        }
    }

    @BeforeEach
    void setUp() throws DataAccessException {
        clearService.clear();
    }

    @Test
    public void clearSuccess() throws UnavailableException, BadRequestException, DataAccessException {
        // Positive
        userService.register(new RegisterRequest("bm888",
                "brickwall", "bm888@byu.edu"));

        ClearResult result = clearService.clear();
        Assertions.assertNotNull(result);
    }

    @Test
    public void registerSuccess() throws UnavailableException, BadRequestException, DataAccessException {
        // Positive
        RegisterResult result = userService.register(new RegisterRequest("bm888",
                "brickwall", "bm888@byu.edu"));
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.authToken());
        Assertions.assertEquals("bm888", result.username());
    }

    @Test
    public void registerFail() throws UnavailableException, BadRequestException, DataAccessException {
        // Negative
        userService.register(new RegisterRequest("bm888",
                "brickwall", "bm888@byu.edu"));

        Assertions.assertThrows(UnavailableException.class, () ->
                userService.register(new RegisterRequest("bm888",
                        "newpassword", "bm888@byu.edu")));

        Assertions.assertThrows(BadRequestException.class, () ->
                userService.register(new RegisterRequest("loser",
                        "password", null)));
    }

    @Test
    public void logoutSuccess()
            throws UnavailableException, BadRequestException, UnauthorizedException, DataAccessException {
        // Positive
        RegisterResult registerResult = userService.register(new RegisterRequest("bm888",
                "brickwall", "bm888@byu.edu"));

        LogoutResult logoutResult = userService.logout(new LogoutRequest(registerResult.authToken()));
        Assertions.assertNotNull(logoutResult);
    }

    @Test
    public void logoutFail() throws UnavailableException, BadRequestException, DataAccessException {
        // Negative
        userService.register(new RegisterRequest("bm888",
                "brickwall", "bm888@byu.edu"));

        Assertions.assertThrows(BadRequestException.class, () -> userService.logout(null));
    }

    @Test
    public void loginSuccess() throws UnavailableException, BadRequestException, UnauthorizedException, DataAccessException {
        // Positive
        RegisterResult registerResult = userService.register(new RegisterRequest("bm888",
                "brickwall", "bm888@byu.edu"));

        userService.logout(new LogoutRequest(registerResult.authToken()));

        LoginResult result = userService.login(new LoginRequest("bm888", "brickwall"));
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.authToken());
        Assertions.assertEquals("bm888", result.username());
    }

    @Test
    public void loginFail() throws UnavailableException, BadRequestException, DataAccessException {
        // Negative
        userService.register(new RegisterRequest("bm888",
                "brickwall", "bm888@byu.edu"));

        Assertions.assertThrows(UnauthorizedException.class,
                () -> userService.login(new LoginRequest("loser", "brickwall")));
    }

    @Test
    public void createSuccess() throws UnauthorizedException,
            BadRequestException, UnavailableException, DataAccessException {
        // Positive
        RegisterResult registerResult = userService.register(new RegisterRequest("bm888",
                "brickwall", "bm888@byu.edu"));

        CreateResult result = gameService.create(
                new CreateRequest("newGame", registerResult.authToken()));
        Assertions.assertNotNull(result);
        Assertions.assertNotEquals(0, result.gameID());
    }

    @Test
    public void createFail() {
        // Negative
        Assertions.assertThrows(UnauthorizedException.class,
                () -> gameService.create(new CreateRequest("newGame", "1234")));
    }

    @Test
    public void listSuccess() throws UnavailableException,
            BadRequestException, UnauthorizedException, DataAccessException {
        // Positive
        RegisterResult registerResult = userService.register(new RegisterRequest("bm888",
                "brickwall", "bm888@byu.edu"));

        gameService.create(new CreateRequest("newGame1", registerResult.authToken()));
        gameService.create(new CreateRequest("newGame2", registerResult.authToken()));
        gameService.create(new CreateRequest("newGame3", registerResult.authToken()));

        ListResult listResult = gameService.list(new ListRequest(registerResult.authToken()));
        Assertions.assertNotNull(listResult);
        Assertions.assertNotNull(listResult.games());
        Assertions.assertEquals(3, listResult.games().size());
    }

    @Test
    public void listFail() throws UnavailableException,
            BadRequestException, UnauthorizedException, DataAccessException {
        // Negative
        RegisterResult registerResult = userService.register(new RegisterRequest("bm888",
                "brickwall", "bm888@byu.edu"));

        gameService.create(new CreateRequest("newGame1", registerResult.authToken()));
        gameService.create(new CreateRequest("newGame2", registerResult.authToken()));
        gameService.create(new CreateRequest("newGame3", registerResult.authToken()));

        Assertions.assertThrows(UnauthorizedException.class,
                () -> gameService.list(new ListRequest("1234")));
    }

    @Test
    public void joinSuccess() throws UnavailableException,
            BadRequestException, UnauthorizedException, DataAccessException {
        // Positive
        RegisterResult registerResult = userService.register(new RegisterRequest("bm888",
                "brickwall", "bm888@byu.edu"));

        CreateResult createResult = gameService.create(
                new CreateRequest("newGame", registerResult.authToken()));
        JoinResult result = gameService.join(
                new JoinRequest("WHITE",
                        createResult.gameID(), registerResult.authToken()));
        Assertions.assertNotNull(result);

        ListResult listResult = gameService.list(new ListRequest(registerResult.authToken()));
        Assertions.assertNotNull(listResult);
        Assertions.assertNotNull(listResult.games());
        Assertions.assertEquals(1, listResult.games().size());
        Assertions.assertEquals("bm888", listResult.games().getFirst().whiteUsername());
    }

    @Test
    public void joinFail() throws UnavailableException,
            BadRequestException, UnauthorizedException, DataAccessException {
        // Negative
        RegisterResult registerResult = userService.register(new RegisterRequest("bm888",
                "brickwall", "bm888@byu.edu"));

        CreateResult createResult = gameService.create(
                new CreateRequest("newGame", registerResult.authToken()));

        gameService.join(new JoinRequest("WHITE",
                createResult.gameID(), registerResult.authToken()));

        Assertions.assertThrows(UnavailableException.class,
                () -> gameService.join(
                        new JoinRequest("WHITE",
                                createResult.gameID(), registerResult.authToken())));
    }
}
