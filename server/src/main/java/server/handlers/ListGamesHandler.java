package server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dataaccess.exceptions.UnauthorizedException;
import model.GameData;
import service.AuthService;
import service.GameService;
import spark.Request;
import spark.Response;

import java.util.Collection;

public class ListGamesHandler {
    private final Gson serializer = new Gson();
    private final ErrorHandler errorHandler = new ErrorHandler();

    public Object listGames(Request req, Response res, GameService gameService, AuthService authService) {
        res.type("application/json");
        try {
            String authToken = req.headers("Authorization");
            authService.validateAuthToken(authToken);
            Collection<GameData> games = gameService.listAllGames();
            Gson gson = new GsonBuilder().serializeNulls().create();
            JsonArray jsonArray = gson.toJsonTree(games).getAsJsonArray();
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("games", jsonArray);

            res.status(200);
            return serializer.toJson(jsonObject);
        }
        catch (UnauthorizedException e) {
            return errorHandler.handleError(e, res, 401);
        }
        catch (Exception e) {
            return errorHandler.handleError(e, res, 500);
        }
    }
}