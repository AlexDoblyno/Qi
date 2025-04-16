package websocketmessages.usercommands;

import model.AuthToken;

import java.util.Objects;

public class UserGameCommand {

    private AuthToken actualAuthToken;

    public UserGameCommand(AuthToken authToken) {
        this.authToken = authToken.getAuthToken();
        this.actualAuthToken = authToken;
    }

    public void setAuthToken(AuthToken trueAuthToken) {
        this.actualAuthToken = trueAuthToken;
    }

    public enum CommandType {
        JOIN_PLAYER,
        JOIN_SPECTATOR,
        MAKE_MOVE,
        LEAVE,
        RESIGN
    }

    protected CommandType commandType;

    private final String authToken;

    public String getAuthString() {
        return authToken;
    }

    public AuthToken getAuthToken() {
        return actualAuthToken;
    }

    public String getUsername() {
        return actualAuthToken.getUsername();
    }

    public CommandType getCommandType() {
        return this.commandType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof UserGameCommand that))
            return false;
        return getCommandType() == that.getCommandType() && Objects.equals(getAuthString(), that.getAuthString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCommandType(), getAuthString());
    }
}
