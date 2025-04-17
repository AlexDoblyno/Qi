package dataaccess;

import model.User;

import java.util.HashMap;

public class UserDAO extends DataAccess {

    // This was for development purposes to test API endpoints with an in-memory database.
    // The database has been shifted to use MySQL, so this file is deprecated and no longer used.

    private HashMap<String, User> users = new HashMap<>();

    public UserDAO() {
        super();
    }

    public void addUser(User user) throws DataAccessException {
        if (userExists(user)) {
            throw new DataAccessException("Error: Username already exists");
        }

        users.put(user.username(), user);
    }

    public User getUser(String username) throws DataAccessException {
        if (!userExists(new User(username, null, null))) {
            throw new DataAccessException("Error: Username does not exist");
        }

        return users.get(username);
    }

    public boolean userExists(User user) {
        return users.containsKey(user.username());
    }

    public void clear() throws DataAccessException {
        users.clear();
    }

}
