package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthToken;

/**
 * The AuthService class represents a service for authenticating users.
 * It extends the Service class.
 */
public class AuthService extends Service {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final UserDAO userDAO;

    public AuthService(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
    }

    /**
     * Validates the given authentication token.
     *
     * @param authToken The authentication token to be validated.
     * @return true if the authentication token is valid, false otherwise.
     * @throws DataAccessException if there is an error accessing the data.
     */
    public boolean validateAuthToken(AuthToken authToken) throws DataAccessException {
        return authDAO.authTokenExists(String.valueOf(authToken));
    }

    /**
     * Retrieves the username associated with the given authentication token.
     *
     * @param authToken the authentication token
     * @return the username associated with the authentication token
     * @throws DataAccessException if there is an error accessing the data
     */
    public String getUsername(AuthToken authToken) throws DataAccessException {
        return authDAO.getUsername(String.valueOf(authToken));
    }
}
