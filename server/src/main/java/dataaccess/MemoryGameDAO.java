package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    HashMap<Object, GameData> gameDB = new HashMap<>();

    @Override
    public void clear() {
        gameDB.clear();
    }

    @Override
    public int createGame(GameData game) {
        gameDB.put(game.gameID(), game);
        return game.gameID();
    }

    @Override
    public GameData getGame(int gameID) {
        return gameDB.get(gameID);
    }

    @Override
    public ArrayList<GameData> listGame() {
        return new ArrayList<>(gameDB.values());
    }

    @Override
    public void updateGame(GameData data) {
        gameDB.put(data.gameID(), data);
    }
}
