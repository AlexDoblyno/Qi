package handler;

import dataaccess.DataAccessException;
import spark.*;
import model.*;
import service.BadRequestException;
import service.UnauthorizedException;
import service.UnavailableException;
import service.UserService;

public class UserHandler implements Handler {

    private final UserService userService;

    public UserHandler() {
        userService = new UserService();
    }

    public Object registerHandler(Request req, Response res)
            throws UnavailableException, BadRequestException, DataAccessException {

        RegisterRequest request = GSON.fromJson(req.body(), RegisterRequest.class);

        RegisterResult result = userService.register(request);

        res.status(200);
        return GSON.toJson(result);
    }

    public Object loginHandler(Request req, Response res)
            throws UnauthorizedException, BadRequestException, DataAccessException {

        LoginRequest request = GSON.fromJson(req.body(), LoginRequest.class);

        LoginResult result = userService.login(request);

        res.status(200);
        return GSON.toJson(result);
    }

    public Object logoutHandler(Request req, Response res)
            throws UnauthorizedException, BadRequestException, DataAccessException {

        String authToken = req.headers("Authorization");

        LogoutRequest request = new LogoutRequest(authToken);

        LogoutResult result = userService.logout(request);

        res.status(200);
        return GSON.toJson(result);
    }
}
