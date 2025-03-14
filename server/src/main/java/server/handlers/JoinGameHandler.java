package server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dataaccess.exceptions.*;
import model.AuthData;
import model.GameData;
import service.AuthService;
import service.GameService;
import spark.Response;
import spark.Request;

public class JoinGameHandler {
    private final Gson serializer = new Gson();
    private final ErrorHandler errorHandler = new ErrorHandler();

    public Object joinGame(Request req, Response res, GameService gameService, AuthService authService) {
        res.type("application/json");
        try {
            String authToken = req.headers("Authorization");
            authService.validateAuthToken(authToken);
            AuthData auth = authService.getUserByAuthToken(authToken);
            String username = auth.username();
            JsonElement bodyJsonElement = JsonParser.parseString(req.body());
            JsonObject jsonObject = bodyJsonElement.getAsJsonObject();
            String playerColor;
            int gameId;
            try {
                playerColor = jsonObject.get("playerColor").getAsString();
                gameId = jsonObject.get("gameID").getAsInt();
            }
            catch (Exception e) {
                return errorHandler.handleError(e, res, 400);
            }
            GameData gameData = gameService.joinGame(gameId, playerColor, username);
            return serializer.toJson(gameData);
        }
        catch (BadRequestException e) {
            return errorHandler.handleError(e, res, 400);
        }
        catch (UserExistsException e) {
            return errorHandler.handleError(e, res, 403);
        }
        catch (UnauthorizedException e) {
            return errorHandler.handleError(e, res, 401);
        }
        catch (Exception e) {
            return errorHandler.handleError(e, res, 500);
        }
    }
}