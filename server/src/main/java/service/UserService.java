package service;

import dataaccess.exceptions.*;
import dataaccess.DataAccessException;
import dataaccess.DAO.UserDAO;
import model.*;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {
    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public UserData createUser(UserData user) throws UserExistsException, BadRequestException, DataAccessException {
        UserData checkUsername = userDAO.getUser(user.username());

        if (checkUsername != null) {
            throw new UserExistsException("already exists");
        }
        else if (user.password() == null || user.password().isEmpty()) {
            throw new BadRequestException("bad request");
        }
        else {
            userDAO.createUser(user);
            return userDAO.getUser(user.username());
        }
    }

    public void validateUser(UserData user) throws UnauthorizedException, DataAccessException {
        UserData validateUser = userDAO.getUser(user.username());

        if (validateUser == null || !BCrypt.checkpw(user.password(), validateUser.password())) {
            throw new UnauthorizedException("unauthorized");
        }
    }

    public void clear() throws BadRequestException, DataAccessException {
        userDAO.clear();
    }

}