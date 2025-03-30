package server.responses;

public class CreateGameResponse {
    protected Integer gameID;

    public CreateGameResponse(Integer gameID) {
        this.gameID = gameID;
    }

    public Integer getGameID() {
        return gameID;
    }
}