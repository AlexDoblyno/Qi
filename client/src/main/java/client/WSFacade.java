package client;

import ResponseExceptionHandler.ResponseException;
import model.AuthToken;
import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ui.GameplayHandlers.GameHandler;
import websocketmessages.usercommands.JoinPlayerCommand;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WSFacade extends Endpoint {

    // Session
    private final Session session;

    private final GameHandler gameHandler;

    private final Gson gson = new GsonBuilder().registerTypeAdapter(ChessGame.class, new ChessGame.ChessGameJson()).create();

    public WSFacade(String url, GameHandler gameHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/connect");
            this.gameHandler = gameHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    gameHandler.handleMessage(message);
                }
            });

        } catch (URISyntaxException | DeploymentException | IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    private void send(JoinPlayerCommand command) throws ResponseException {
        try {
            session.getBasicRemote().sendText(gson.toJson(command));
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void sendJoinPlayer(AuthToken authToken, Integer gameID, ChessGame.TeamColor playerColor) throws ResponseException {
//        var command = new websocket.commands.JoinPlayerCommand(authToken, gameID, playerColor);
//        send(command); TODO
    }

    public void sendJoinSpectator(AuthToken authToken, Integer gameID) throws ResponseException {
//        var command = new JoinSpectatorCommand(authToken, gameID);
//        send(command); TODO
    }

    public void sendMove(AuthToken authToken, Integer gameID, ChessMove move) throws ResponseException {
//        var command = new websocketmessages.usercommands.MoveCommand(authToken, gameID, move);
//        send(command); TODO
    }

    public void sendLeaveGame(AuthToken authToken, Integer gameID) throws ResponseException {
//        var command = new websocketmessages.usercommands.LeaveCommand(authToken, gameID);
//        send(command); TODO
    }

    public void sendResign(AuthToken authToken, Integer gameID) throws ResponseException {
//        var command = new websocketmessages.usercommands.ResignCommand(authToken, gameID);
//        send(command); TODO
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}