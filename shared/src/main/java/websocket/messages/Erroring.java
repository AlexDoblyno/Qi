package websocket.messages;

public class Erroring extends ServerMessage {
    private final String errorMessage;

    public Erroring(ServerMessageType type, String errorMessage) {
        super(type);
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
