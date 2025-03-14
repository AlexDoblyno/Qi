package dataaccess;

import model.AuthData;

import javax.xml.crypto.Data;

public interface AuthDAO {
    void clear() throws DataAccessException;

    AuthData createAuth(String username) throws DataAccessException;

    AuthData getAuth(AuthData auth) throws DataAccessException;

    String getUsername(AuthData auth) throws DataAccessException;

    void deleteAuth(AuthData auth) throws DataAccessException;

    int getDBSize() throws DataAccessException;
}
