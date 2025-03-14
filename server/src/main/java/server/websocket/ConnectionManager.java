package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Session, Connection> connections = new ConcurrentHashMap<>();

    public void add(int gameID, Session session) {
        var connection = new Connection(gameID, session);
        connections.put(session, connection);
    }

    public void self(int gameID, Session currentSession, ServerMessage notification) throws IOException {
        for (var c : connections.values()) {
            if (c.session.isOpen() && c.gameID == gameID) {
                if (c.session.equals(currentSession)) {
                    String jsonMessage = new Gson().toJson(notification);
                    c.send(jsonMessage);
                }
            }
        }
    }

    public void remove(Session session) {
        connections.remove(session);
    }

    public void broadcast(int gameID, Session currentSession, ServerMessage notification) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen() && c.gameID == gameID) {
                if (!c.session.equals(currentSession)) {
                    String jsonMessage = new Gson().toJson(notification);
                    c.send(jsonMessage);
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c.gameID);

        }
    }
}
