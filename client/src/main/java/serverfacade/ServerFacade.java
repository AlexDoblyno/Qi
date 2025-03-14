package serverfacade;

import com.google.gson.Gson;
import exception.ResponseException;
import model.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Map;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public AuthData registerUser(UserData data) throws ResponseException {
        var path = "/user";
        return this.makeRequest("POST", path, data, AuthData.class, "");
    }

    public AuthData loginUser(UserData data) throws ResponseException {
        var path = "/session";
        return this.makeRequest("POST", path, data, AuthData.class, "");
    }

    public void logoutUser(AuthData auth) throws ResponseException {
        var path = "/session";
        this.makeRequest("DELETE", path, null, null, auth.authToken());
    }

    public GameList listGames(AuthData auth) throws ResponseException {
        var path = "/game";
        return this.makeRequest("GET", path, null, GameList.class, auth.authToken());
    }

    public GameData createGame(GameData gameName, AuthData auth) throws ResponseException {
        var path = "/game";
        return this.makeRequest("POST", path, gameName, GameData.class, auth.authToken());
    }

    public void joinGame(JoinGameRequest join, AuthData auth) throws ResponseException {
        var path = "/game";
        this.makeRequest("PUT", path, join, null, auth.authToken());
    }

    public void clear() throws ResponseException {
        var path = "/db";
        this.makeRequest("DELETE", path, null, null, "");
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String auth) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            if (!auth.isEmpty()) {
                http.addRequestProperty("Authorization", auth);
            }

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respBody = http.getErrorStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                Map response = new Gson().fromJson(reader, Map.class);
                throw new ResponseException(status, "failure: " + response.get("message"));
            }
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
