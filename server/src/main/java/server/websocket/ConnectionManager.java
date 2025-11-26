package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class ConnectionManager {
    public final HashMap<Integer, HashSet<Session>> connections = new HashMap<>();

    public void add(int gameID, Session session) {
        HashSet<Session> sessions = connections.get(gameID);
        if (sessions == null) {
            sessions = new HashSet<>();
            connections.put(gameID, sessions);
        }
        sessions.add(session);
    }

    public void remove(int gameID, Session session) {
        var sessions = connections.get(gameID);
        if (sessions != null) {
            sessions.remove(session);
        } else {
            System.err.println("Could not remove session");
        }
    }

    public void broadcast(int gameID, Session excludedSession, ServerMessage serverMessage) throws IOException {
        String msg = serverMessage.toString();
        var sessions = connections.get(gameID);
        if (sessions != null) {
            for (Session s : sessions) {
                if (s.isOpen()) {
                    if (!s.equals(excludedSession)) {
                        s.getRemote().sendString(msg);
                    }
                }
            }
        }
    }
}
