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