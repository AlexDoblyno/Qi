package dataaccess;

import model.AuthData;

import java.util.UUID;
import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }


    HashMap<String, AuthData> authDB = new HashMap<>();


    @Override
    public AuthData createAuth(String username) {
        String token = generateToken();
        AuthData data = new AuthData(token, username);
        authDB.put(token, data);
        return data;
    }

    @Override
    public AuthData getAuth(AuthData auth) {
        if (auth == null || auth.authToken() == null) {
            return null;
        }
        return authDB.get(auth.authToken());
    }

    @Override
    public String getUsername(AuthData auth) {
        AuthData retrievedAuth = authDB.get(auth.authToken());
        return retrievedAuth != null ? retrievedAuth.username() : null;


    }

    @Override
    public void deleteAuth(AuthData auth) {
        if (authDB.containsKey(auth.authToken())) {
            authDB.remove(auth.authToken());
        }
    }

    @Override
    public void clear() {
        authDB.clear();
    }

    public int getDBSize() {
        return authDB.size();
    }
}

