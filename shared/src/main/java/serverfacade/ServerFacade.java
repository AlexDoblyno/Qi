package serverfacade;

import com.google.gson.Gson;
import ResponseExceptionHandler.ResponseException;
import model.AuthToken;
import model.ModelDeserializer;
import requestclasses.JoinGameRequest;
import resultclasses.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public void clear() throws ResponseException {
        makeRequest("DELETE", "/db", null, ClearDBResult.class, null);
    }

    public LoginResult login(String username, String password) throws ResponseException {
        var request = new requestclasses.LoginRequest(username, password);
        return makeRequest("POST", "/session", request, LoginResult.class, null);
    }

    public RegisterResult register(String username, String password, String email) throws ResponseException {
        var request = new requestclasses.RegisterRequest(username, password, email);
        return makeRequest("POST", "/user", request, RegisterResult.class, null);
    }

    public void logout(AuthToken authToken) throws ResponseException {
        makeRequest("DELETE", "/session", authToken, LogoutResult.class, authToken);
    }

    public ListGamesResult listGames(AuthToken authToken) throws ResponseException {
        return makeRequest("GET", "/game", authToken, ListGamesResult.class, authToken);
    }

    public JoinGameResult joinGame(AuthToken authToken, Integer gameID, String playerColor) throws ResponseException {
        var request = new JoinGameRequest(authToken.getAuthToken(), gameID, playerColor);
        return makeRequest("PUT", "/game", request, JoinGameResult.class, authToken);
    }

    public CreateGameResult createGame(AuthToken authToken, String gameName) throws ResponseException {
        // If authToken is null throw a server exception
        var request = new requestclasses.CreateGameRequest(authToken.getAuthToken(), gameName);
        return makeRequest("POST", "/game", request, CreateGameResult.class, authToken);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, AuthToken authToken) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            if (authToken != null) {
                http.setRequestProperty("Authorization", authToken.getAuthToken());
            }
            http.setDoOutput(true);

            if (!method.equals("GET")) {
                writeBody(request, http);
            }
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
            throw new ResponseException(status, "failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = ModelDeserializer.deserialize(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
