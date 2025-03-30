package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MemAuthDAO implements AuthDAO {
    private static Map<String, AuthData> authentications = new HashMap<String, AuthData>();

    @Override
    public void clear() throws DataAccessException {
        authentications.clear();
    }

    @Override
    public String getUsername(String authToken) throws DataAccessException {
        return authentications.get(authToken).username();
    }

    @Override
    public String createAuthToken(String username) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        AuthData authentication = new AuthData(username, authToken);
        authentications.put(authToken, authentication);
        return authToken;
    }

    @Override
    public void deleteAuthToken(String authToken) throws DataAccessException {
        authentications.remove(authToken);
    }

    @Override
    public Integer getSize() throws DataAccessException {
        return authentications.size();
    }

    @Override
    public Boolean verifyAuthToken(String authToken) throws DataAccessException {
        return authentications.containsKey(authToken);
    }
}
