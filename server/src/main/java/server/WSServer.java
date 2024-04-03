package server;

import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import spark.Spark;
import com.google.gson.Gson;
import webSocketMessages.userCommands.*;

record UserGameCommandRaw(String authToken, String commandType) {}

@WebSocket
public class WSServer {

    Gson gson = new Gson();

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
        System.out.println("Message received: " + message);

        UserGameCommandRaw rc = gson.fromJson(message, UserGameCommandRaw.class);
        UserGameCommand c = null;

        System.out.println("Command type: " + rc.commandType());

        switch (rc.commandType()) {
            case "JOIN_PLAYER" -> c = gson.fromJson(message, JoinPlayer.class);
            case "JOIN_OBSERVER" -> c = gson.fromJson(message, JoinObserver.class);
            case "MAKE_MOVE" -> c = gson.fromJson(message, MakeMove.class);
            case "LEAVE" -> c = gson.fromJson(message, Leave.class);
            case "RESIGN" -> c = gson.fromJson(message, Resign.class);
            default -> System.out.println("Unknown command type: " + rc.commandType());
        }

        System.out.println("Command: " + c);
    }
}
