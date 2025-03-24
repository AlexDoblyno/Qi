package ui;

import chess.*;
import com.google.gson.Gson;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Scanner;

public class GameUI {

    private final Client client; // WebSocket client
    private final Scanner scanner;
    private final Integer gameID; // Game ID the user is connected to
    private final String authToken;
    private final ChessGame.TeamColor color;
    private WebSocketClient webSocketClient;
    private ChessGame chessGame;
    private static boolean closed = false;

    public GameUI(Client client, Integer gameID, ChessGame.TeamColor teamColor, String authToken) throws Exception {
        this.client = client;
        this.authToken = authToken;
        this.gameID = gameID;
        this.scanner = new Scanner(System.in);
        this.color = teamColor;
        try {
            this.webSocketClient = client.getServerFacade().createWebSocketClient(this);
        } catch (Exception e) {
            throw new Exception("Error creating web socket client");
        }
        try {
            UserGameCommand joinCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            webSocketClient.sendMessage(joinCommand);
        } catch (Exception e) {
            throw new Exception("Error joining web socket server");
        }
        if (teamColor == null) {
            webSocketClient.teamColor = ChessGame.TeamColor.WHITE;
        } else {
            webSocketClient.teamColor = teamColor;
        }
    }