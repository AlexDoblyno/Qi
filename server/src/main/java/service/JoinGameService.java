package service;

import dataaccess.AuthSQL;
import dataaccess.DataAccessException;
import dataaccess.GameSQL;
import model.Game;
import requestclasses.JoinGameRequest;
import resultclasses.JoinGameResult;
import server.ServerException;

import java.util.Objects;

public class JoinGameService extends Service {

    public JoinGameService() {
        super();
    }

    public JoinGameService(AuthSQL authSQL, GameSQL gameSQL, UserSQL userSQL) {
        super(authSQL, gameSQL, userSQL);
    }

    public JoinGameResult joinGame(JoinGameRequest request) throws ServerException {
        // Check if authentication token or gameID are null
        if (request.getAuthToken() == null) {
            return new JoinGameResult(400, "JoinGame Error: bad request");
        }

        // Check if authentication token is valid
        try {
            authSQL.checkAuthToken(request.getAuthToken());
        } catch (Exception e) {
            return new JoinGameResult(401, "Error: unauthorized");
        }

        Game game = null;

        // Check if gameID exists
        try {
            game = gameSQL.getGame(String.valueOf(request.getGameID()));
        } catch (Exception e) {
            return new JoinGameResult(400, "Error: game does not exist");
        }

        // Discover Username
        String username;
        try {
            username = authSQL.getUsername(request.getAuthToken());
        } catch (Exception e) {
            throw new ServerException("Error: " + e.getMessage(), 500);
        }

        // Join game
        try {
            if (Objects.equals(request.getPlayerColor(), "WHITE")) {
                if (game.getWhiteUsername() == null) {
                    game.setWhiteUsername(username);
                    gameSQL.updateGame(game);
                    return new JoinGameResult(200, "JoinGame Success");
                } else {
                    throw new ServerException("Error: already taken", 403);
                }
            } else if (Objects.equals(request.getPlayerColor(), "BLACK")) {
                if (game.getBlackUsername() == null) {
                    game.setBlackUsername(username);
                    gameSQL.updateGame(game);
                    return new JoinGameResult(200, "JoinGame Success");
                } else {
                    throw new ServerException("Error: already taken", 403);
                }
            } else {
                throw new ServerException("Error: bad request", 400);
            }
        } catch (DataAccessException e) {
            throw new ServerException("Error: " + e.getMessage(), 500);
        } catch (ServerException e) {
            throw e;
        }
    }
}