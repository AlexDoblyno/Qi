package websocketmessages.usercommands;

import model.AuthToken;

public class JoinSpectatorCommand extends UserGameCommand {
    private Integer gameID;

    public JoinSpectatorCommand(AuthToken authToken, Integer gameID) {
        super(authToken);
        this.gameID = gameID;
        this.commandType = CommandType.JOIN_SPECTATOR;
    }

    public Integer getGameID() {
        return gameID;
    }

    public void setGameID(Integer gameID) {
        this.gameID = gameID;
    }
}
