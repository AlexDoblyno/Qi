package ui;

import chess.ChessGame;
import com.google.gson.Gson;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

@ClientEndpoint
public class WebSocketClient {
    private Session session;
    private final Gson gson = new Gson();
    private ChessGame game;
    public ChessGame.TeamColor teamColor = ChessGame.TeamColor.WHITE;
    private final GameUI gameUI;

    // Constructor: Connects to the server
    public WebSocketClient(String serverUri, GameUI gameUI) {
        this.gameUI = gameUI;
        connect(serverUri);
    }

    private void connect(String serverUri) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, new URI(serverUri));
            System.out.println("Connected to WebSocket server at: " + serverUri);
        } catch (Exception e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
            // Optionally retry or handle connection failures
        }
    }


    // Called when the WebSocket connection is established
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
    }

    // Called when a message is received from the server
    @OnMessage
    public void onMessage(String message) {
        if (message.contains("LOAD_GAME")) {
            LoadGameMessage msg = gson.fromJson(message, LoadGameMessage.class);
            handleLoadGame(msg.getGame());
        } else if (message.contains("NOTIFICATION")) {
            NotificationMessage msg = gson.fromJson(message, NotificationMessage.class);
            handleNotification(msg.getMessage());
        } else if (message.contains("ERROR")) {
            ErrorMessage msg = gson.fromJson(message, ErrorMessage.class);
            handleError(msg.getErrorMessage());
        } else {
            System.err.println("Unknown server message type.");
        }
        System.out.print("\r[IN_GAME] >>> ");
    }

    // Send a message to the server
    public void sendMessage(UserGameCommand command) {
        try {
            if (session != null && session.isOpen()) {
                String json = gson.toJson(command);
                session.getBasicRemote().sendText(json);
            } else {
                System.err.println("Cannot send message: WebSocket session is closed.");
            }
        } catch (Exception e) {
            System.err.println("Failed to send message: " + e.getMessage());
        }
    }

    // Close the WebSocket connection
    public void close() {
        try {
            if (session != null && session.isOpen()) {
                session.close();
            }
        } catch (Exception e) {
            System.err.println("Failed to close WebSocket: " + e.getMessage());
        }
    }

    // Handle LOAD_GAME messages
    private void handleLoadGame(ChessGame game) {
        gameUI.loadGame(game);
    }

    // Handle ERROR messages
    private void handleError(String errorMessage) {
        System.err.println("Error from server: " + errorMessage);
    }

    // Handle NOTIFICATION messages
    private void handleNotification(String message) {
        System.out.println("\rNotification: " + message);
        // Display the notification in the UI or console
    }
}
