package dataaccess;

import model.UserData;

public interface UserDAO {
    void clear() throws DataAccessException;

    void createUser(UserData data) throws DataAccessException;

    model.UserData getUser(String username) throws DataAccessException;

    int getDBSize() throws DataAccessException;
}
