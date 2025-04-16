package requestclasses;

public class CreateGameRequest extends Request {

    private String gameName;

    public CreateGameRequest() {
        super();
        this.gameName = null;
    }

    public CreateGameRequest(String authToken, String gameName) {
        super(authToken);
        this.gameName = gameName;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }
}