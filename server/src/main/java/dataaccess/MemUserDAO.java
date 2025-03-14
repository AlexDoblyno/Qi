package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MemUserDAO implements UserDAO {
    protected static Map<String, UserData> users = new HashMap<>();

    @Override
    public void createUser(String username, String password, String email) throws DataAccessException {
        UserData user = new UserData(username, password, email);
        users.put(username, user);
    }

    @Override
    public UserData getUser(String username, String password) throws DataAccessException {
        if (users.containsKey(username) && Objects.equals(users.get(username).password(), password)) {
            return users.get(username);
        }
        return null;
    }

    @Override
    public void clear() throws DataAccessException {
        users.clear();
    }

    @Override
    public Integer getSize() throws DataAccessException {
        return users.size();
    }

    @Override
    public Boolean verifyUser(String username) throws DataAccessException {
        return users.containsKey(username);
    }

    @Override
    public String getPassword(String username) throws DataAccessException {
        return users.get(username).password();
    }
}
