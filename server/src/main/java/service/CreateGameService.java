package service;

import dataaccess.AuthSQL;
import dataaccess.DataAccessException;
import dataaccess.GameSQL;
import requestclasses.CreateGameRequest;
import resultclasses.CreateGameResult;
import server.ServerException;

public class CreateGameService extends Service {

    public CreateGameService() {
        super();
    }

    public CreateGameService(AuthSQL authSQL, GameSQL gameSQL, UserSQL userSQL) {
        super(authSQL, gameSQL, userSQL);
    }

    public CreateGameResult createGame(CreateGameRequest request) throws DataAccessException, ServerException {
        //Check if authentication token or gameID are null
        if (request.getAuthToken() == null || request.getGameName() == null) {
            return new CreateGameResult(400, "Error: bad request");
        }

        //Check if authentication token is valid
        try {
            var token = authSQL.checkAuthToken(request.getAuthToken());
        } catch (DataAccessException e) {
            return new CreateGameResult(401, "Error: unauthorized");
        }

        //Create game
        try {
            var game = new model.Game();
            game.setGameName(request.getGameName());
            return new CreateGameResult(Integer.toString(gameSQL.addGame(game)));
        } catch (Exception e) {
            return new CreateGameResult(500, "Error: " + e.getMessage());
        }
    }
}