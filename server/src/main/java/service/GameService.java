package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import com.google.gson.Gson;
import model.JoinGameRequest;

import java.util.Map;
import java.util.Collection;

public class GameService {
    GameDAO gameDAO;
    AuthDAO authDAO;
    int id;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public Collection<GameData> listGames(AuthData auth) throws DataAccessException {
        var info = authDAO.getAuth(auth);
        if (info == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        return gameDAO.listGame();
    }

    public int createGame(String gameName, AuthData auth) throws DataAccessException {
        var tempList = gameDAO.listGame();
        for (int i = 0; i <= tempList.size(); i++) {
            id++;
        }

        if (gameName == null) {
            throw new DataAccessException("Error: bad request");
        }

        ChessGame board = new ChessGame();
        GameData gameData = new GameData(id, null, null, gameName, board, false);

        gameDAO.createGame(gameData);


        return id;
    }

    public void joinGame(JoinGameRequest info, AuthData auth) throws DataAccessException {
        var authenticatedUser = authDAO.getAuth(auth);
        if (authenticatedUser == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        if (info.gameID() == null) {
            throw new DataAccessException("Error: bad request");
        }
        GameData data = gameDAO.getGame(info.gameID());
        if (data == null) {
            throw new DataAccessException("Error: bad request");
        }

        if (info.playerColor() == null) {
            throw new DataAccessException("Error: bad request");
        }

        if (info.playerColor().equals("BLACK") || info.playerColor().equals("black")) {
            if (data.blackUsername() != null) {
                throw new DataAccessException("Error: already taken");
            }
            GameData game = new GameData(data.gameID(), data.whiteUsername(), auth.username(), data.gameName(), data.game(), data.gameOver());
            gameDAO.updateGame(game);
        } else {
            if (data.whiteUsername() != null) {
                throw new DataAccessException("Error: already taken");
            }
            GameData game = new GameData(data.gameID(), auth.username(), data.blackUsername(), data.gameName(), data.game(), data.gameOver());
            gameDAO.updateGame(game);
        }

    }

    public void clear() throws DataAccessException {
        gameDAO.clear();
    }
}