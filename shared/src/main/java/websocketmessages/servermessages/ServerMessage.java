package websocketmessages.servermessages;

import java.util.Objects;

public class ServerMessage {
    ServerMessageType serverMessageType;

    public void setServerMessageType(ServerMessageType serverMessageType) {
        this.serverMessageType = serverMessageType;
    }

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    public ServerMessage(ServerMessageType type) {
        this.serverMessageType = type;
    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerMessage that = (ServerMessage) o;
        return serverMessageType == that.serverMessageType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(serverMessageType);
    }
}
