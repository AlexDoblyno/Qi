package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public String loginUser(String username, String password) throws DataAccessException {
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            throw new IllegalArgumentException("Username and password are required");
        }
        if (!userDAO.verifyUser(username)) {
            throw new DataAccessException("No user found");
        } else if (!BCrypt.checkpw(password, userDAO.getPassword(username))) {
            throw new DataAccessException("Wrong password");
        }
        return authDAO.createAuthToken(username);
    }

    public void logoutUser(String token) throws DataAccessException {
        if (!authDAO.verifyAuthToken(token)) {
            throw new DataAccessException("Invalid token");
        }
        authDAO.deleteAuthToken(token);
    }
}
