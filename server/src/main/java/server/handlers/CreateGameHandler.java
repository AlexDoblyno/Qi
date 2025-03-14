package server.handlers;

import com.google.gson.Gson;
import dataaccess.exceptions.*;
import model.GameData;
import service.AuthService;
import service.GameService;
import spark.Request;
import spark.Response;
import java.util.Map;

public class CreateGameHandler {
    private final Gson serializer = new Gson();
    ErrorHandler errorHandler = new ErrorHandler();

    public Object createGame(Request request, Response response, GameService gameService, AuthService authService) {
        response.type("application/json");
        try {
            String authToken = request.headers("Authorization");
            authService.validateAuthToken(authToken);
            GameData newGameData = serializer.fromJson(request.body(), GameData.class);
            int newGameID = gameService.createGame(newGameData).gameID();
            response.status(200);
            return serializer.toJson(Map.of("gameID", newGameID));
        }
        catch (UnauthorizedException exception) {
            return errorHandler.handleError(exception, response, 401);
        }
        catch (BadRequestException exception) {
            return errorHandler.handleError(exception, response, 400);
        }
        catch (Exception exception) {
            return errorHandler.handleError(exception, response, 500);
        }
    }
}