package service;

import dataaccess.exceptions.*;
import dataaccess.DataAccessException;
import dataaccess.DAO.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {
    private AuthService authService;
    private AuthDAO authDAO;
    private UserDAO userDAO;

    @BeforeEach
    public void setup() throws DataAccessException, BadRequestException {
        authDAO = new AuthDAO();
        userDAO = new UserDAO();
        authService = new AuthService(authDAO);
        authDAO.clear();
        userDAO.clear();
    }

    @Test
    void loginUser() throws DataAccessException, UnauthorizedException {
        UserData checkUser = new UserData("user1", "password", null);
        userDAO.createUser(checkUser);
        AuthData authData = authService.loginUser(checkUser);
        assertNotNull(authData);
    }

    @Test
    void logoutUser() throws UnauthorizedException, DataAccessException {
        UserData checkUser = new UserData("user1", "password", null);
        userDAO.createUser(checkUser);
        AuthData authData = authService.loginUser(checkUser);
        String authToken = authData.authToken();
        authService.logoutUser(authToken);
    }

    @Test
    void getUserByAuthToken() throws UnauthorizedException, DataAccessException {
        UserData checkUser = new UserData("user1", "password", null);
        userDAO.createUser(checkUser);
        AuthData authData = authService.loginUser(checkUser);
        String authToken = authData.authToken();
        AuthData findUser = authService.getUserByAuthToken(authToken);
        assertNotNull(findUser);
    }

    @Test
    void validateAuthToken() throws UnauthorizedException, DataAccessException {
        UserData checkUser = new UserData("user1", "password", null);
        userDAO.createUser(checkUser);
        AuthData authData = authService.loginUser(checkUser);
        String authToken = authData.authToken();
        authService.validateAuthToken(authToken);
    }

    @Test
    void clear() throws UnauthorizedException, DataAccessException, BadRequestException {
        UserData checkUser = new UserData("user1", "password", null);
        userDAO.createUser(checkUser);
        AuthData authData = authService.loginUser(checkUser);
        String authToken = authData.authToken();
        authService.clear();
        assertNull(authService.getUserByAuthToken(authToken));
    }
}