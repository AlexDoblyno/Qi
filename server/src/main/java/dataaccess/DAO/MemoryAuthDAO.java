package dataaccess.DAO;

import model.AuthData;

import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDAO {
    private final Map<AuthData, String> authentications = new HashMap<>();

    public MemoryAuthDAO() {}

    public AuthData createAuth(String username) {
        AuthData auth = new AuthData(AuthData.createToken(), username);
        authentications.put(auth, username);
        return auth;
    }

    public AuthData getAuthByToken(String token) {
        for (AuthData auth : authentications.keySet()) {
            if (auth.authToken().equals(token)) {
                return auth;
            }
        }
        return null;
    }

    public void deleteAuth(String authToken) {
        AuthData auth = getAuthByToken(authToken);
        authentications.remove(auth);
    }

    public void clear() {
        authentications.clear();
    }

}