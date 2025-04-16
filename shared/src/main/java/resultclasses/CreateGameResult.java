package resultclasses;

public class CreateGameResult extends Result {

    private String gameID;

    public CreateGameResult() {
        super();
        this.gameID = null;
    }

    public CreateGameResult(String gameID) {
        super(200);
        this.gameID = gameID;
    }

    public CreateGameResult(int status, String message) {
        super(status, message);
        this.gameID = null;
    }

    public String getGameID() {
        return gameID;
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }
}
