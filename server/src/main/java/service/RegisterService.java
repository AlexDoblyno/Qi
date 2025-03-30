package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import org.mindrot.jbcrypt.BCrypt;

public class RegisterService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public RegisterService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public String createUser(String username, String password, String email) throws DataAccessException {
        if (username == null || password == null || email == null || username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            throw new IllegalArgumentException("Username, password, and email are required");
        }
        if (userDAO.verifyUser(username)) {
            throw new DataAccessException("Username is already taken");
        }
        userDAO.createUser(username, BCrypt.hashpw(password, BCrypt.gensalt()), email);
        return authDAO.createAuthToken(username);
    }

}
