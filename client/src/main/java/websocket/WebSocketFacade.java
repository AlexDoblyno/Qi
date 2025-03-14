package websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import exception.ResponseException;
import model.GameData;
import ui.GameplayUI;
import websocket.commands.MakeMove;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;
    private ChessGame currentGame;
    private ChessGame.TeamColor teamColor;


    public WebSocketFacade(String url, NotificationHandler notificationHandler, ChessGame.TeamColor teamColor, ChessGame currentGame) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;
            this.currentGame = currentGame;
            this.teamColor = teamColor;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    System.out.println(message);
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    try {
                        switch (serverMessage.getServerMessageType()) {
                            case NOTIFICATION:
                                handleNotification(message);
                                break;
                            case ERROR:
                                handleError(message);
                                break;
                            case LOAD_GAME:
                                System.out.println("We have entered the Load game case");
                                handleLoadGame(message);
                                break;
                        }
                    } catch (Exception ex) {
                        throw new RuntimeException("I am still getting things together");
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void handleNotification(String message) throws ResponseException {
        try {
            var action = new Gson().fromJson(message, Notifying.class);
            System.out.println(action.getMessage());
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }


    public void handleError(String serverMessage) throws ResponseException {
        try {
            var action = new Gson().fromJson(serverMessage, Erroring.class);
            System.out.println(action.getErrorMessage());
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void handleLoadGame(String serverMessage) throws ResponseException {
        try {
            var action = new Gson().fromJson(serverMessage, Loading.class);
            System.out.println(action.getColor());
            currentGame = action.game;
            GameData gameData = new GameData(0, null, null, null, currentGame, false);
            redraw(gameData, teamColor);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void connectGame(String authToken, int gameID) throws IOException {
        var action = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
        this.session.getBasicRemote().sendText(new Gson().toJson(action));
    }

    public void makeMove(String authToken, int gameID, ChessMove move) throws IOException {
        var action = new MakeMove(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, move);
        this.session.getBasicRemote().sendText(new Gson().toJson(action));
        System.out.println("This happend");
    }

    public void leaveGame(String authToken, int gameID) throws IOException {
        var action = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
        this.session.getBasicRemote().sendText(new Gson().toJson(action));
        this.session.close();
    }

    public void resignGame(String authToken, int gameID) throws IOException {
        var action = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
        this.session.getBasicRemote().sendText(new Gson().toJson(action));
        this.session.close();
    }

    public void redraw(GameData gameData, ChessGame.TeamColor perspective) {
        GameData newGameData = new GameData(0, null, null, null, currentGame, false);
        GameplayUI.displayGame(newGameData, perspective, null);
    }

    public void highlight(GameData gameData, ChessGame.TeamColor colorChoice, ChessPosition startPosition) {
        GameData newGameData = new GameData(0, null, null, null, currentGame, false);
        GameplayUI.displayGame(newGameData, colorChoice, startPosition);
    }
}
