package service;

import chess.ChessGame;
import dataaccess.*;
import model.GameData;

import java.util.List;

public class GameService {
    private UserDAO userDAO;
    private GameDAO gameDAO;
    private AuthDAO authDAO;

    public GameService(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public void verifyAuthToken(String authToken) throws DataAccessException {
        if (!authDAO.verifyAuthToken(authToken)) {
            throw new DataAccessException("Invalid Auth Token");
        }
    }

    public List<GameData> listGames(String authToken) throws DataAccessException {
        if (!authDAO.verifyAuthToken(authToken)) {
            throw new DataAccessException("Invalid Auth Token");
        }
        return gameDAO.listGames();
    }

    public Integer createGame(String gameName) throws DataAccessException {
        if (gameName == null || gameName.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid parameter");
        }

        return gameDAO.createGame(gameName);
    }

    public void joinGame(String authToken, Integer gameID, ChessGame.TeamColor playerColor) throws Exception {
        String username = authDAO.getUsername(authToken);
        if (gameID == null) {
            throw new IllegalArgumentException("Invalid gameID");
        }
        if (!gameDAO.verifyGame(gameID)) {
            throw new IllegalArgumentException("Error: bad request");
        }

        GameData gameData = gameDAO.getGame(gameID);


        if ((playerColor != ChessGame.TeamColor.WHITE) && (playerColor != ChessGame.TeamColor.BLACK)) {
            throw new IllegalArgumentException("Error: Invalid team color");
        }

        if ((playerColor == ChessGame.TeamColor.WHITE && gameData.whiteUsername() != null)
                || (playerColor == ChessGame.TeamColor.BLACK && gameData.blackUsername() != null)) {
            throw new IllegalAccessException("Error: Player color already taken.");
        }

        gameDAO.updateGame(playerColor, gameID, username);
    }
}
