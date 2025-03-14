package service;

import dataaccess.exceptions.*;
import dataaccess.DataAccessException;
import dataaccess.DAO.*;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private UserDAO userDAO;
    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private UserService userService;

    @BeforeEach
    void setUp() throws BadRequestException {
        userDAO = new UserDAO();
        gameDAO = new GameDAO();
        authDAO = new AuthDAO();
        userService = new UserService(userDAO);
        userDAO.clear();
    }

    @Test
    void createValidUser() throws UserExistsException, BadRequestException, UnauthorizedException, DataAccessException {
        UserData userData = new UserData("bob", "password", "email@email.com");
        userService.createUser(userData);
        userService.validateUser(userData);
    }

    @Test
    void createInvalidUser() throws UserExistsException, BadRequestException {
        UserData userData = new UserData("bob", null, "email@email.com");
        assertThrows(BadRequestException.class, () -> userService.createUser(userData));
    }

    @Test
    void createDuplicateUser() throws UserExistsException, BadRequestException, UnauthorizedException, DataAccessException {
        UserData userData = new UserData("bob", "password", "email@email.com");
        userService.createUser(userData);
        assertThrows(UserExistsException.class, () -> userService.createUser(userData));
    }

    @Test
    void clear() throws UserExistsException, BadRequestException, UnauthorizedException, DataAccessException {
        UserData userData = new UserData("bob", "password", "email@email.com");
        userService.createUser(userData);
        userService.clear();
        assertThrows(UnauthorizedException.class, () -> userService.validateUser(userData));
    }
}