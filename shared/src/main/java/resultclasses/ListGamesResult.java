package resultclasses;

import model.GameData;

public class ListGamesResult extends Result {

    private GameData[] games;

    public ListGamesResult() {
        super();
    }

    public ListGamesResult(GameData[] games) {
        super(200);
        this.games = games;
    }

    public ListGamesResult(int status, String message) {
        super(status, message);
    }

    public GameData[] getGames() {
        return games;
    }
}
