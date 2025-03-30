package server;

import com.google.gson.Gson;
import dataaccess.*;
import model.GameData;
import server.requests.*;
import server.responses.*;
import service.*;
import spark.*;

import java.util.List;

public class Server {

    private RegisterService registerService;
    private ClearService clearService;
    private UserService userService;
    private GameService gameService;
    private final Gson gson = new Gson();

    public int run(int desiredPort) {

        UserDAO userDAO = new SqlUserDAO();
        GameDAO gameDAO = new SqlGameDAO();
        AuthDAO authDAO = new SqlAuthDAO();

        registerService = new RegisterService(userDAO, authDAO);
        clearService = new ClearService(gameDAO, authDAO, userDAO);
        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(userDAO, gameDAO, authDAO);

        Spark.port(desiredPort);

        Spark.webSocket("/ws", new WebSocketServer(authDAO,gameDAO,userDAO));

        Spark.staticFiles.location("web");

        try {
            DatabaseManager.createTables();
        } catch (Exception e) {}

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::registerHandler);
        Spark.delete("/db", this::clearHandler);
        Spark.post("/session", this::loginHandler);
        Spark.delete("/session", this::logoutHandler);
        Spark.get("/game", this::listGamesHandler);
        Spark.post("/game", this::createGameHandler);
        Spark.put("/game", this::joinGameHandler);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        //Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object joinGameHandler(Request request, Response response) {
        try {
            JoinGameRequest joinGameRequest = gson.fromJson(request.body(), JoinGameRequest.class);
            String authToken = request.headers("Authorization");
            gameService.verifyAuthToken(authToken);

            gameService.joinGame(authToken, joinGameRequest.gameID(), joinGameRequest.playerColor());
            response.status(200);
            response.body("");
            return "";
        } catch (DataAccessException e) {
            response.status(401);
            return "{\"message\": \"Error: unauthorized\"}";
        } catch (IllegalArgumentException e) {
            response.status(400);
            return "{\"message\": \"Error: bad request\"}";
        } catch (IllegalAccessException e) {
            response.status(403);
            return "{\"message\": \"Error: bad request\"}";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private Object createGameHandler(Request request, Response response) {
        try {
            String authToken = request.headers("Authorization");
            gameService.verifyAuthToken(authToken);

            Integer gameID = gameService.createGame(gson.fromJson(request.body(), GameRequest.class).gameName());

            CreateGameResponse gameResponse = new CreateGameResponse(gameID);
            response.status(200);
            response.type("application/json");

            return gson.toJson(gameResponse);
        } catch (DataAccessException e) {
            response.status(401);
            return "{\"message\": \"Error: unauthorized\"}";
        } catch (IllegalArgumentException e) {
            response.status(400);
            return "{\"message\": \"Error: bad request\"}";
        }
    }

    private Object listGamesHandler(Request request, Response response) {
        try {
            String authToken = request.headers("Authorization");
            gameService.verifyAuthToken(authToken);
            List<GameData> gamesList = gameService.listGames(authToken);

            ListGamesResponse listGamesResponse = new ListGamesResponse(gamesList);
            response.status(200);
            response.type("application/json");

            return gson.toJson(listGamesResponse);
        } catch (DataAccessException e) {
            response.status(401);
            return "{\"message\": \"Error: unauthorized\"}";
        } catch (IllegalArgumentException e) {
            response.status(400);
            return "{\"message\": \"Error: bad request\"}";
        }
    }

    private Object logoutHandler(Request request, Response response) {
        try {
            String authToken = request.headers("Authorization");

            userService.logoutUser(authToken);

            response.status(200);
            return "";
        } catch (DataAccessException error) {
            response.status(401);
            response.body("{\"message\": \"Error: unauthorized\"}");
            return "{\"message\": \"Error: unauthorized\"}";
        }
    }

    private Object loginHandler(Request request, Response response) {
        try {
            LoginRequest loginRequest = gson.fromJson(request.body(), LoginRequest.class);

            String authToken = userService.loginUser(loginRequest.getUsername(), loginRequest.getPassword());
            LoginResponse loginResponse = new LoginResponse(loginRequest.getUsername(), authToken);

            response.type("application/json");
            response.status(200);

            return gson.toJson(loginResponse);
        } catch (DataAccessException error) {
            response.status(401);
            response.body("{\"message\": \"Error: unauthorized\"}");
            return "{\"message\": \"Error: unauthorized\"}";
        }
    }

    private Object clearHandler(Request request, Response response) {
        try {
            clearService.clear();

            response.status(200);
            response.body("");
            return "";
        } catch (DataAccessException error) {
            response.status(500);
            response.body("{\"message\": \"Error: no object found\"}");
            return "{\"message\": \"Error: no object found\"}";
        }
    }

    private Object registerHandler(Request req, Response res) {
        try {
            RegisterRequest regReq = gson.fromJson(req.body(), RegisterRequest.class);

            String authToken = registerService.createUser(regReq.getUsername(), regReq.getPassword(),
                    regReq.getEmail());

            RegisterResponse regRes = new RegisterResponse(regReq.getUsername(), authToken);

            res.type("application/json");
            res.status(200);

            return gson.toJson(regRes);
        } catch (DataAccessException error) {
            res.status(403);
            res.body("{\"message\": \"Error: username already in use\"}");
            return "{\"message\": \"Error: username already in use\"}";
        } catch (IllegalArgumentException error) {
            res.status(400);
            res.body("{\"message\": \"Error: Invalid username, email, or password\"}");
            return "{\"message\": \"Error: Invalid username, email, or password\"}";
        }
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    public void clear() throws DataAccessException {
        clearService.clear();
    }
}
