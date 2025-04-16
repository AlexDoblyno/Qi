package websocketmessages.usercommands;

import model.AuthToken;

public class ResignCommand extends UserGameCommand {
    private Integer gameID;

    public ResignCommand(AuthToken authToken, Integer gameID) {
        super(authToken);
        this.gameID = gameID;
        this.commandType = CommandType.RESIGN;
    }

    public Integer getGameID() {
        return gameID;
    }

    public void setGameID(Integer gameID) {
        this.gameID = gameID;
    }
}
