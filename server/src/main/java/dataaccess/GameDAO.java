package dataaccess;

import model.Game;

import java.util.HashMap;

public class GameDAO extends DataAccess {

    // This was for development purposes to test API endpoints with an in-memory database.
    // The database has been shifted to use MySQL, so this file is deprecated and no longer used.

    private HashMap<String, Game> games = new HashMap<>();

    private int gameID;

    public GameDAO() {
        super();
        gameID = 1;
    }

    public void addGame(Game game) throws DataAccessException {
        game.setGameID(Integer.toString(gameID));
        incrementGameID();
        games.put(game.getGameID(), game);
    }

    public Game getGame(String gameID) throws DataAccessException {
        return games.get(gameID);
    }

    private void incrementGameID(){
        gameID++;
    }

    public Game[] getAllGames() throws DataAccessException {
        return (Game[]) games.values().toArray(new Game[0]);
    }

    public Game updateGame(Game game) throws DataAccessException {
        return games.put(game.getGameID(), game);
    }

    public void clear(){
        games.clear();
    }

}
