package server.handlers;

import com.google.gson.Gson;
import dataaccess.exceptions.UnauthorizedException;
import service.AuthService;
import spark.Request;
import spark.Response;

public class LogoutHandler {
    private final Gson serializer = new Gson();
    private final ErrorHandler errorHandler = new ErrorHandler();

    public Object logout(Request request, Response response, AuthService authService) throws UnauthorizedException {
        response.type("application/json");
        try {
            String authToken = request.headers("Authorization");
            authService.logoutUser(authToken);
            response.status(200);
            return serializer.toJson(new Object());
        }
        catch (UnauthorizedException e) {
            return errorHandler.handleError(e, response, 401);
        }
        catch (Exception e) {
            return errorHandler.handleError(e, response, 500);
        }
    }

}