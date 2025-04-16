package requestclasses;

public class JoinGameRequest extends Request {

    private Integer gameID;
    private String playerColor;

    public JoinGameRequest() {
        super();
        this.gameID = null;
        this.playerColor = null;
    }

    public JoinGameRequest(String authToken, Integer gameID, String playerColor) {
        super(authToken);
        this.gameID = gameID;
        this.playerColor = playerColor;
    }

    public int getGameID() {
        return gameID;
    }

    public void setGameID(Integer gameID) {
        this.gameID = gameID;
    }

    public String getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(String playerColor) {
        this.playerColor = playerColor;
    }
}