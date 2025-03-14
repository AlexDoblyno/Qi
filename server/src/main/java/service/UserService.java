package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import javax.xml.crypto.Data;

public class UserService {
    UserDAO userDAO;
    AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData register(UserData user) throws DataAccessException {
        if (user.username() == null || user.password() == null || user.email() == null) {
            throw new DataAccessException("Error: bad request");
        }
        UserData data = userDAO.getUser(user.username());
        if (data != null) {
            throw new DataAccessException("Error: already taken");
        }
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        UserData newUser = new UserData(user.username(), hashedPassword, user.email());
        userDAO.createUser(newUser);
        AuthData auth = authDAO.createAuth(newUser.username());
        if (auth == null) {
            throw new DataAccessException(("Error: bad request"));
        }
        return authDAO.getAuth(auth);
    }

    public AuthData login(UserData user) throws DataAccessException {
        var info = userDAO.getUser(user.username());
        if (info == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        String hashedPassword = info.password();
        if (BCrypt.checkpw(user.password(), hashedPassword)) {
            AuthData auth = authDAO.createAuth(user.username());
            return authDAO.getAuth(auth);
        } else {
            throw new DataAccessException("Error: unauthorized");
        }
    }

    public void logout(AuthData auth) throws DataAccessException {
        if (auth == null) {
            throw new DataAccessException(("Error: unauthorized"));
        }
        var info = authDAO.getAuth(auth);
        if (info == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        authDAO.deleteAuth(auth);
    }

    public void clear() throws DataAccessException {
        userDAO.clear();
    }
}
