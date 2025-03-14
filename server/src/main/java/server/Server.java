package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import handler.ClearHandler;
import handler.GameHandler;
import handler.UserHandler;
import service.BadRequestException;
import service.UnauthorizedException;
import service.UnavailableException;
import spark.*;

import java.util.Map;

public class Server {

    private final ClearHandler clearHandler;
    private final UserHandler userHandler;
    private final GameHandler gameHandler;

    public Server() {
        this.clearHandler = new ClearHandler();
        this.userHandler = new UserHandler();
        this.gameHandler = new GameHandler();
        try {
            DataAccess.configureDatabase();
        } catch (DataAccessException e) {
            System.err.println("Warning: Database initialization failed: " + e.getMessage());
        }
    }

    public Server(ClearHandler clearHandler, UserHandler userHandler, GameHandler gameHandler) {
        this.clearHandler = clearHandler;
        this.userHandler = userHandler;
        this.gameHandler = gameHandler;
        try {
            DataAccess.configureDatabase();
        } catch (DataAccessException e) {
            System.err.println("Warning: Database initialization failed: " + e.getMessage());
        }
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", clearHandler::clearHandler);
        Spark.post("/user", userHandler::registerHandler);
        Spark.post("/session", userHandler::loginHandler);
        Spark.delete("/session", userHandler::logoutHandler);
        Spark.get("/game", gameHandler::listHandler);
        Spark.post("/game", gameHandler::createHandler);
        Spark.put("/game", gameHandler::joinHandler);

        Spark.exception(Exception.class, this::errorHandler);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    @SuppressWarnings("IfCanBeSwitch")
    public void errorHandler(Exception e, Request req, Response res) {

        var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage()), "success", false));

        int status;
        if (e instanceof DataAccessException) {
            status = 500;
        } else if (e instanceof BadRequestException) {
            status = 400;
        } else if (e instanceof UnauthorizedException) {
            status = 401;
        } else if (e instanceof UnavailableException) {
            status = 403;
        } else {
            status = 666;
            System.out.println(body);
        }

        res.type("application/json");
        res.status(status);
        res.body(body);
    }
}
