package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.Game;

public class GameService extends Service {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final UserDAO userDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
    }

    public Game getGame(Integer gameID) throws DataAccessException {
        return gameDAO.getGame(String.valueOf(gameID));
    }

    public void updateGame(Game game) throws DataAccessException {
        gameDAO.updateGame(game);
    }
}
