package dataaccess;

import chess.*;
import model.GameData;

import java.util.List;

public interface GameDAO {
    void clear() throws DataAccessException;

    Integer createGame(String gameName) throws DataAccessException;

    GameData getGame(Integer gameID) throws DataAccessException;

    List<GameData> listGames() throws DataAccessException;

    void updateGame(ChessGame.TeamColor playerColor, Integer gameID, String username) throws DataAccessException;

    void updateChessGame(ChessGame game, Integer gameID) throws DataAccessException;

    Integer getSize() throws DataAccessException;

    public Boolean verifyGame(Integer gameID) throws DataAccessException;
}
