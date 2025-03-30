package websocket.messages;

public class ErrorMessage extends ServerMessage {
    private String errorMessage;

    public ErrorMessage(ServerMessageType type, String errorMessage) {
        super(type);
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
