package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

public class ConnectionManager {
    public final HashMap<Integer, Set<Session>> connections = new HashMap<>();

    public void add(int gameID, Session session) {
        var sessions = connections.get(gameID);
        sessions.add(session);
    }

    public void remove(int gameID, Session session) {
        var sessions = connections.get(gameID);
        sessions.remove(session);
    }

    public void broadcast(int gameID, Session excludedSession, ServerMessage serverMessage) throws IOException {
        String msg = serverMessage.toString();
        var sessions = connections.get(gameID);
        for (Session s : sessions) {
            if (s.isOpen()) {
                if (!s.equals(excludedSession)) {
                    s.getRemote().sendString(msg);
                }
            }
        }
    }
}
