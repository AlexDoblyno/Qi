package dataaccess.DAO;

import model.GameData;
import chess.ChessGame;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.security.SecureRandom;

public class MemoryGameDAO {
    private final Map<Integer, GameData> games = new HashMap<>();

    public MemoryGameDAO() {
    }

    public GameData createGame(String gameName) {
        int newGameID = generateNewGameID();

        GameData newGameData = new GameData(
                newGameID,
                null,
                null,
                gameName,
                new ChessGame()
        );

        games.put(newGameID, newGameData);
        return newGameData;
    }

    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    public Collection<GameData> readAllGames() {
        return games.values();
    }

    public void updateGame(GameData game) {
        games.put(game.gameID(), game);
    }

    public void clear() {
        games.clear();
    }

    private int generateNewGameID() {
        SecureRandom random = new SecureRandom();
        int randomID = random.nextInt(Integer.MAX_VALUE);
        while (games.containsKey(randomID)) {
            randomID = random.nextInt(Integer.MAX_VALUE);
        }
        return randomID;
    }
}