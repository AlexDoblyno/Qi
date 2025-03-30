package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public class ClearService {
    private static GameDAO gameDAO;
    private static AuthDAO authDAO;
    private static UserDAO userDAO;

    public ClearService(GameDAO gameDAO, AuthDAO authDAO, UserDAO userDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public void clear() throws DataAccessException {
        gameDAO.clear();
        authDAO.clear();
        userDAO.clear();
    }
}
