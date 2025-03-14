package websocket;


import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

public interface NotificationHandler {
    void notify(ServerMessage serverMessage);
}
