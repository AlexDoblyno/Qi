package ui;

import chess.ChessGame;

import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerFacade {

    private String baseUrl;

    public ServerFacade(String port) {
        this.baseUrl = "http://localhost:" + port + "/";
    }

    public String playGame(String gameID, ChessGame.TeamColor playerColor, String authToken) throws Exception {
        String uri = baseUrl + "game";
        String json = "{\"playerColor\":\"" + playerColor.toString() + "\",\"gameID\":\"" + gameID + "\"}";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(uri))
                .header("Content-Type", "application/json")
                .header("Authorization", authToken)
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public String createGame(String gameName, String authToken) throws Exception {
        String uri = baseUrl + "game";
        String json = "{\"gameName\":\"" + gameName + "\"}";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(uri))
                .header("Content-Type", "application/json")
                .header("Authorization", authToken)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public String listGames(String authToken) throws Exception {
        String uri = baseUrl + "game";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .uri(new URI(uri))
                .header("Authorization", authToken)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public String logout(String authToken) throws Exception {
        String uri = baseUrl + "session";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(uri))
                .header("Content-Type", "application/json")
                .header("Authorization", authToken)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public String registerUser(String username, String password, String email) throws Exception {
        String uri = baseUrl + "user";
        String json = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\",\"email\":\"" + email + "\"}";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(uri))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public String login(String username, String password) throws Exception {
        String uri = baseUrl + "session";
        String json = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(uri))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public WebSocketClient createWebSocketClient(GameUI gameUI) throws Exception {
        String uri = baseUrl + "ws";
        uri = uri.replaceFirst("http", "ws");
        WebSocketClient webSocketClient = new WebSocketClient(uri, gameUI);
        return webSocketClient;
    }
}
