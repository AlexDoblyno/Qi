package server.websocket;

import chess.ChessMove;
import chess.ChessGame;
import chess.ChessPiece;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.SqlAuthDAO;
import dataaccess.SqlGameDAO;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.MakeMove;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    SqlAuthDAO authDAO;
    SqlGameDAO gameDAO;

    public WebSocketHandler(SqlAuthDAO authDAO, SqlGameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        System.out.println(message);
        var action = new Gson().fromJson(message, UserGameCommand.class);
        try {
            switch (action.getCommandType()) {
                case CONNECT -> connectGame(action.getAuthToken(), action.getGameID(), session);
                case MAKE_MOVE -> makeMove(message, action.getGameID(), session, action.getAuthToken());
                case LEAVE -> leaveGame(action.getAuthToken(), action.getGameID(), session);
                case RESIGN -> resignGame(action.getAuthToken(), action.getGameID(), session);
            }
        } catch (Exception ex) {
            errorHandler(ex.getMessage(), action.getGameID(), session);
        }

    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        connections.remove(session);
    }

    private void connectGame(String authToken, int gameID, Session session) throws IOException, DataAccessException {
        AuthData auth = new AuthData(authToken, null);
        String username = authDAO.getUsername(auth);
        if (username == null) {
            errorHandler("Error: Auth Token is not valid", gameID, session);
        } else {
            GameData game = gameDAO.getGame(gameID);

            //Adds the connection via session
            connections.add(gameID, session);

            //Create a notification message for broadcasting
            var message = String.format("%s has joined the game", username);
            var notification = new Notifying(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(gameID, session, notification);

            ChessGame.TeamColor color = null;
            if (game.blackUsername() != null && game.blackUsername().equals(username)) {
                color = ChessGame.TeamColor.BLACK;
            } else {
                color = ChessGame.TeamColor.WHITE;
            }

            //Create the "Load Game" message for the root client
            var loadGame = new Loading(ServerMessage.ServerMessageType.LOAD_GAME, game.game(), color);
            connections.self(gameID, session, loadGame);
        }
    }

    private void makeMove(String message, int gameID, Session session, String authToken) throws IOException, DataAccessException {
        AuthData auth = new AuthData(authToken, null);
        String username = authDAO.getUsername(auth);
        if (username == null) {
            throw new DataAccessException("Error: Auth Token is not valid");
        }
        var info = new Gson().fromJson(message, MakeMove.class);
        try {
            //Get the Game info and Make the Move
            GameData game = gameDAO.getGame(gameID);
            ChessMove move = info.getMove();

            if (game.getGameOver()) {
                throw new DataAccessException("Error: Game is Over");
            }

            var currentTurn = game.game().getTeamTurn();
            System.out.println(currentTurn);
            if (game.whiteUsername() != null && !game.whiteUsername().equals(username) && currentTurn.equals(ChessGame.TeamColor.WHITE)) {
                throw new DataAccessException("Error: It isn't your turn");
            } else if (game.blackUsername() != null && !game.blackUsername().equals(username) && currentTurn.equals(ChessGame.TeamColor.BLACK)) {
                throw new DataAccessException("Error: It isn't your turn");
            }

            game.game().makeMove(move);
            gameDAO.updateGame(game);

            ChessGame.TeamColor color = null;
            if (game.blackUsername() != null && game.blackUsername().equals(username)) {
                color = ChessGame.TeamColor.BLACK;
            } else {
                color = ChessGame.TeamColor.WHITE;
            }

            //Load the Game for everyone
            var loadGame = new Loading(ServerMessage.ServerMessageType.LOAD_GAME, game.game(), color);
            connections.self(gameID, session, loadGame);
            connections.broadcast(gameID, session, loadGame);

            //Notify the other players
            var messaging = String.format("%s has made a move", username);
            var notifying = new Notifying(ServerMessage.ServerMessageType.NOTIFICATION, messaging);
            connections.broadcast(gameID, session, notifying);

            ChessPiece piece = game.game().getBoard().getPiece(move.getEndPosition());
            var currentColor = piece.getTeamColor();

            //Check whether a color is in checkmate
            if (game.game().isInCheckmate(ChessGame.TeamColor.BLACK)) {
                var messageStuff = "Black is in Checkmate";
                GameData gameData1 = new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), game.game(), true);
                gameDAO.updateGame(gameData1);
                var notification = new Notifying(ServerMessage.ServerMessageType.NOTIFICATION, messageStuff);
                connections.broadcast(gameID, session, notification);
                connections.self(gameID, session, notification);
            } else if (game.game().isInCheckmate(ChessGame.TeamColor.WHITE)) {
                var messageStuff = "White is in Checkmate";
                GameData gameData1 = new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), game.game(), true);
                gameDAO.updateGame(gameData1);
                var notification = new Notifying(ServerMessage.ServerMessageType.NOTIFICATION, messageStuff);
                connections.broadcast(gameID, session, notification);
                connections.self(gameID, session, notification);
            }
            //Check whether a color is in stalemate
            else if (game.game().isInStalemate(ChessGame.TeamColor.BLACK)) {
                var messageStuff = "Black is in Stalemate";
                GameData gameData1 = new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), game.game(), true);
                gameDAO.updateGame(gameData1);
                var notification = new Notifying(ServerMessage.ServerMessageType.NOTIFICATION, messageStuff);
                connections.broadcast(gameID, session, notification);
                connections.self(gameID, session, notification);
            } else if (game.game().isInStalemate(ChessGame.TeamColor.WHITE)) {
                var messageStuff = "White is in Stalemate";
                GameData gameData1 = new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), game.game(), true);
                gameDAO.updateGame(gameData1);
                var notification = new Notifying(ServerMessage.ServerMessageType.NOTIFICATION, messageStuff);
                connections.broadcast(gameID, session, notification);
                connections.self(gameID, session, notification);
            }
            //Check Whether a Color is in Check
            else if (game.game().isInCheck(ChessGame.TeamColor.BLACK)) {
                var messageStuff = "Black is in Check";
                var notification = new Notifying(ServerMessage.ServerMessageType.NOTIFICATION, messageStuff);
                connections.broadcast(gameID, session, notification);
                connections.self(gameID, session, notification);
            } else if (game.game().isInCheck(ChessGame.TeamColor.WHITE)) {
                var messageStuff = "White is in Check";
                var notification = new Notifying(ServerMessage.ServerMessageType.NOTIFICATION, messageStuff);
                connections.broadcast(gameID, session, notification);
                connections.self(gameID, session, notification);
            }


        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            errorHandler(ex.getMessage(), gameID, session);
        }

    }

    public void leaveGame(String authToken, int gameID, Session session) throws IOException, DataAccessException {
        AuthData auth = new AuthData(authToken, null);
        String username = authDAO.getUsername(auth);
        GameData game = gameDAO.getGame(gameID);

        var message = String.format("%s left the game", username);
        //System.out.println(message);
        var notification = new Notifying(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(gameID, session, notification);

        if (game.whiteUsername() != null) {
            if (game.whiteUsername().equals(username)) {
                GameData gameData1 = new GameData(game.gameID(), null, game.blackUsername(), game.gameName(), game.game(), game.gameOver());
                //System.out.println("Updating game with GAMEID: " + game.gameID());
                gameDAO.updateGame(gameData1);
            }
        } else if (game.blackUsername() != null) {
            if (game.blackUsername().equals(username)) {
                GameData gameData1 = new GameData(game.gameID(), game.whiteUsername(), null, game.gameName(), game.game(), game.gameOver());
                //System.out.println("Updating game with GAMEID: " + game.gameID());
                gameDAO.updateGame(gameData1);
            }
        }

        //Remove the Connection
        connections.remove(session);

    }

    public void resignGame(String authToken, int gameID, Session session) throws DataAccessException, IOException {
        AuthData auth = new AuthData(authToken, null);
        String username = authDAO.getUsername(auth);
        GameData game = gameDAO.getGame(gameID);

        if (!game.whiteUsername().equals(username) && !game.blackUsername().equals(username)) {
            throw new DataAccessException("Error: You can't resign");
        }

        //Creates the broadcast message
        var message = String.format("%s resigned the game", username);
        var notification = new Notifying(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.self(gameID, session, notification);
        connections.broadcast(gameID, session, notification);

        //Update Game Data
        GameData gameData = gameDAO.getGame(gameID);
        if (gameData.whiteUsername().equals(username)) {
            GameData gameData1 = new GameData(gameData.gameID(), null, gameData.blackUsername(), gameData.gameName(), gameData.game(), true);
            gameDAO.updateGame(gameData1);
        } else if (gameData.blackUsername().equals(username)) {
            GameData gameData1 = new GameData(gameData.gameID(), gameData.whiteUsername(), null, gameData.gameName(), gameData.game(), true);
            gameDAO.updateGame(gameData1);
        }

        //Create the root client message


        //Remove the Connection
        connections.remove(session);
    }

    public void errorHandler(String message, int gameID, Session session) throws IOException {
        //System.out.println(message);
        if (message.equals("Error: Game ID is not valid")) {
            connections.add(gameID, session);
            var errorMessage = String.format("Error: You have entered an incorrect ID");
            var errorNotification = new Erroring(ServerMessage.ServerMessageType.ERROR, errorMessage);
            connections.self(gameID, session, errorNotification);
            connections.remove(session);
        } else if (message.equals("Error: Auth Token is not valid")) {
            connections.add(gameID, session);
            var errorMessage = String.format("Error: Auth Token is not valid");
            var errorNotification = new Erroring(ServerMessage.ServerMessageType.ERROR, errorMessage);
            connections.self(gameID, session, errorNotification);
            connections.remove(session);
        } else if (message.equals("Invalid Move")) {
            connections.add(gameID, session);
            var errorMessage = String.format("Error: You have made an invalid move");
            var errorNotification = new Erroring(ServerMessage.ServerMessageType.ERROR, errorMessage);
            connections.self(gameID, session, errorNotification);
            connections.remove(session);
        } else if (message.equals("Error: Game is Over")) {
            connections.add(gameID, session);
            var errorMessage = String.format("Error: You can't make a move. The Game is Over");
            var errorNotification = new Erroring(ServerMessage.ServerMessageType.ERROR, errorMessage);
            connections.self(gameID, session, errorNotification);
            connections.remove(session);
        } else if (message.equals("Error: It isn't your turn")) {
            connections.add(gameID, session);
            var errorMessage = String.format("Error: It isn't your turn");
            var errorNotification = new Erroring(ServerMessage.ServerMessageType.ERROR, errorMessage);
            connections.self(gameID, session, errorNotification);
            connections.remove(session);
        } else if (message.equals("Error: You can't resign")) {
            connections.add(gameID, session);
            var errorMessage = String.format("Error: You can't resign. You aren't in the game");
            var errorNotification = new Erroring(ServerMessage.ServerMessageType.ERROR, errorMessage);
            connections.self(gameID, session, errorNotification);
            connections.remove(session);
        }
    }
}
