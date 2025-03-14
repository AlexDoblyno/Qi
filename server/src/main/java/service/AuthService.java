package service;

import dataaccess.DAO.AuthDAO;
import dataaccess.exceptions.*;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;

public class AuthService {
    private final AuthDAO authDAO;

    public AuthService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public AuthData loginUser(UserData user) throws UnauthorizedException, DataAccessException {
        return authDAO.createAuth(user.username());
    }

    public void logoutUser(String authToken) throws UnauthorizedException, DataAccessException {
        AuthData authData = authDAO.getAuthByToken(authToken);
        if (authData == null) {
            throw new UnauthorizedException("unauthorized");
        }
        else {
            authDAO.deleteAuth(authToken);
        }
    }

    public AuthData getUserByAuthToken(String authToken) throws UnauthorizedException, DataAccessException {
        return authDAO.getAuthByToken(authToken);
    }

    public void validateAuthToken(String authToken) throws UnauthorizedException, DataAccessException {
        AuthData authData = authDAO.getAuthByToken(authToken);
        if (authData == null || !authData.authToken().equals(authToken)) {
            throw new UnauthorizedException("unauthorized");
        }
    }

    public void clear() throws BadRequestException {
        authDAO.clear();
    }
}