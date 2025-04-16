package websocketmessages.usercommands;

import model.AuthToken;

public class LeaveCommand extends UserGameCommand {
    private Integer gameID;

    public LeaveCommand(AuthToken authToken, Integer gameID) {
        super(authToken);
        this.gameID = gameID;
        this.commandType = CommandType.LEAVE;
    }

    public Integer getGameID() {
        return gameID;
    }

    public void setGameID(Integer gameID) {
        this.gameID = gameID;
    }
}
