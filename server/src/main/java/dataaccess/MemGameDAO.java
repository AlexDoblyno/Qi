package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemGameDAO implements GameDAO {
    private static Integer newID = 0;
    private static Map<Integer, GameData> games = new HashMap<Integer, GameData>();

    @Override
    public void clear() throws DataAccessException {
        games.clear();
    }

    @Override
    public Integer createGame(String gameName) throws DataAccessException {
        newID++;
        games.put(newID, new GameData(newID, null, null, gameName, new ChessGame()));
        return newID;
    }

    @Override
    public GameData getGame(Integer gameID) throws DataAccessException {
        return games.get(gameID);
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        List<GameData> gameList = new ArrayList<GameData>();
        for (Map.Entry<Integer, GameData> entry : games.entrySet()) {
            gameList.add(entry.getValue());
        }
        return gameList;
    }

    @Override
    public void updateGame(ChessGame.TeamColor playerColor, Integer gameID, String username)
            throws DataAccessException {
        if (playerColor == ChessGame.TeamColor.BLACK) {
            GameData gameData = games.get(gameID);
            String whiteUsername = gameData.whiteUsername();
            String gameName = gameData.gameName();
            ChessGame game = gameData.game();
            games.remove(gameID);
            games.put(gameID, new GameData(gameID, whiteUsername, username, gameName, game));
        } else {
            GameData gameData = games.get(gameID);
            String blackUsername = gameData.blackUsername();
            String gameName = gameData.gameName();
            ChessGame game = gameData.game();
            games.remove(gameID);
            games.put(gameID, new GameData(gameID, username, blackUsername, gameName, game));
        }
    }

    @Override
    public void updateChessGame(ChessGame game, Integer gameID) throws DataAccessException {
        return;
    }

    public Boolean verifyGame(Integer gameID) throws DataAccessException {
        return games.get(gameID) != null;
    }

    @Override
    public Integer getSize() throws DataAccessException {
        return games.size();
    }
}
