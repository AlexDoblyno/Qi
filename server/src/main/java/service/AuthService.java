package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;

public class AuthService {
    AuthDAO authDAO;

    public AuthService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public void clear() throws DataAccessException {
        authDAO.clear();
    }

    public String getUsername(AuthData auth) throws DataAccessException {
        String str = authDAO.getUsername(auth);
        return str;
    }


}
