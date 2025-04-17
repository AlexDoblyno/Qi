package server;

import dataaccess.*;
import spark.Request;
import spark.Response;
import spark.Spark;

public class Server {
    private final AuthSQL authSQL = new AuthSQL();
    private final GameSQL gameSQL = new GameSQL();
    private final UserSQL userSQL = new UserSQL();
    private final LoginService loginService;
    private final ClearDBService clearDBService;
    private final RegisterService registerService;
    private final CreateGameService createGameService;
    private final JoinGameService joinGameService;
    private final ListGamesService listGamesService;
    private final LogoutService logoutService;
    private final LoginHandler loginHandler;
    private final LogoutHandler logoutHandler;
    private final ClearDBHandler clearDBHandler;
    private final RegisterHandler registerHandler;
    private final CreateGameHandler2 createGameHandler;
    private final JoinGameHandler joinGameHandler;
    private final ListGamesHandler listGamesHandler;

    public static void main(String[] args) {
        (new Server()).run(8080);
    }

    public Server() {
        this.loginService = new LoginService(this.authSQL, this.userSQL);
        this.clearDBService = new ClearDBService(this.authSQL, this.gameSQL, this.userSQL);
        this.createGameService = new CreateGameService(this.authSQL, this.gameSQL, this.userSQL);
        this.joinGameService = new JoinGameService(this.authSQL, this.gameSQL, this.userSQL);
        this.listGamesService = new ListGamesService(this.authSQL, this.gameSQL, this.userSQL);
        this.registerService = new RegisterService(this.authSQL, this.userSQL);
        this.logoutService = new LogoutService(this.authSQL, this.userSQL);
        this.loginHandler = new LoginHandler(this.loginService);
        this.clearDBHandler = new ClearDBHandler(this.clearDBService);
        this.registerHandler = new RegisterHandler(this.registerService);
        this.createGameHandler = new CreateGameHandler2(this.createGameService);
        this.joinGameHandler = new JoinGameHandler(this.joinGameService);
        this.listGamesHandler = new ListGamesHandler(this.listGamesService);
        this.logoutHandler = new LogoutHandler(this.logoutService);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        defineRoutes();

        Spark.exception(ServerException.class, this::handleException);

        Spark.awaitInitialization();
        return Spark.port();
    }

    private void defineRoutes() {
        Spark.delete("/db", this.clearDBHandler);
        Spark.post("/user", this.registerHandler);
        Spark.post("/session", this.loginHandler);
        Spark.delete("/session", this.logoutHandler);
        Spark.post("/game", this.createGameHandler);
        Spark.get("/game", this.listGamesHandler);
        Spark.put("/game", this.joinGameHandler);
    }

    private void handleException(ServerException exception, Request request, Response response) {
        response.status(exception.getStatusCode());
        response.body(exception.getMessage());
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

}