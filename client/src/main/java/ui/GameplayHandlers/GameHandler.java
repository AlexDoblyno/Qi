package ui.GameplayHandlers;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ui.ChessBoardPrinter;
import ui.Printer;
import websocket.messages.ServerMessage;


public class GameHandler {

    // Pretty Printers
    protected final Printer p = new Printer();
    protected final ChessBoardPrinter cbp = new ChessBoardPrinter();

    protected ChessGame game;

    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    private ChessGame.TeamColor teamColor;

    public void setTeamColor(ChessGame.TeamColor teamColor) {
        this.teamColor = teamColor;
    }

    public void updateGame(ChessGame game) {
        this.game = game;

        //Update the board
        p.println("");
        cbp.printBoard(game.getBoard(), this.getTeamColor());
        p.reset();
        p.setColor(Printer.Color.GREEN);
        p.print(">>> ");
    }

    public void printMessage(String message) {
        p.reset();
        p.setColor(Printer.Color.LIGHT_GREY);
        p.println("");
        p.println("[Server] " + message);
        p.setColor(Printer.Color.GREEN);
        p.print(">>> ");
    }

    public void printError(String message) {
        p.reset();
        p.setColor(Printer.Color.RED);
        p.println("");
        p.println(message);
        p.setColor(Printer.Color.GREEN);
        p.print(">>> ");
    }

    public void handleMessage(String message) {
        var serverMessage = new Gson().fromJson(message, ServerMessage.class);
        switch (serverMessage.getServerMessageType()) {
            case LOAD_GAME -> {
                // Load game
                var game = new GsonBuilder().registerTypeAdapter(ChessGame.class, new ChessGame.ChessGameTA()).create()
                        .fromJson(message, websocketmessages.servermessages.LoadGameMessage.class);
                updateGame(game.getGame());
            }
            case NOTIFICATION -> {
                // Print notification
                var notification = new Gson().fromJson(message, websocketmessages.servermessages.NotificationMessage.class);
                printMessage(notification.getMessage());
            }
            case ERROR -> {
                // Print error
                var error = new Gson().fromJson(message, websocketmessages.servermessages.ErrorMessage.class);
                printError(error.getErrorMessage());
            }
            default -> {
            }
        }
    }

}
