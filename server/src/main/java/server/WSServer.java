package server;

import chess.ChessGame;
import dataAccess.*;
import exceptions.UnauthorizedException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import service.ClearService;
import service.GameService;
import service.UserService;
import com.google.gson.Gson;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

record UserGameCommandRaw(String authToken, String commandType) {}

@WebSocket
public class WSServer {

    Gson gson = new Gson();

    // DAOs
    GameDAO gameDao;
    AuthDAO authDao;
    UserDAO userDao;

    // Services
    ClearService clearService;
    UserService userService;
    GameService gameService;

    // List of game clients per game
    HashMap<Integer, ArrayList<Session>> gameSessions = new HashMap<>();

    public WSServer() {
        // Initialize DAOs and services
        try {
            userDao = new SqlUserDAO();
            authDao = new SqlAuthDAO();
            gameDao = new SqlGameDAO();

            clearService = new ClearService(gameDao, authDao, userDao);
            userService = new UserService(authDao, userDao);
            gameService = new GameService(authDao, userDao, gameDao);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
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
            default -> {
                System.out.println("Unknown command type: " + rc.commandType());
                return;
            }
        }

        System.out.println("Command: " + c);

        // Check auth token
        if (c.getAuthString() == null) {
            System.out.println("No auth token provided");
            new Error("Error: no auth token provided").send(session);
            return;
        }

        // Handle the command
        try {
            switch (c.getCommandType()) {
                case JOIN_PLAYER -> joinPlayerHandler(session, (JoinPlayer) c);
                case JOIN_OBSERVER -> joinObserverHandler(session, (JoinObserver) c);
                case MAKE_MOVE -> System.out.println("Make move");
                case LEAVE -> leaveHandler(session, (Leave) c);
                case RESIGN -> System.out.println("Resign");
                default -> System.out.println("Unknown command type: " + c.getCommandType());
            }
        } catch (IOException e) {
            System.out.println("Error handling command: " + e.getMessage());
            e.printStackTrace();
        }
    }

    void sendLoadGame(Session session, String authToken, int gameID) throws IOException {
        System.out.println("Sending game data");
        try {
            GameData g = gameService.getGame(authToken, gameID);
            System.out.println("Game data: " + g);
            new LoadGame(g.game()).send(session);
            // TODO send to all other clients in game
        } catch (UnauthorizedException e) {
            System.out.println("Error sending game data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    void addClientToGame(int gameID, Session session) {
        if (!gameSessions.containsKey(gameID)) {
            ArrayList<Session> sessions = new ArrayList<>();
            sessions.add(session);
            gameSessions.put(gameID, sessions);
        } else {
            gameSessions.get(gameID).add(session);
        }
    }

    void sendToGameClients(int gameID, ServerMessage message, Session excludeSession) {
        ArrayList<Session> sessions = gameSessions.get(gameID);
        if (sessions != null) {
            for (Session s : sessions) {
                if (s == excludeSession) {
                    continue;
                }
                try {
                    message.send(s);
                } catch (IOException e) {
                    System.out.println("Error sending message to client: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    void joinPlayerHandler(Session session, JoinPlayer command) throws IOException {
        try {

            // get auth data
            AuthData a = userService.getAuth(command.getAuthString());

            // get game data
            GameData g = gameService.getGame(command.getAuthString(), command.gameID);

            // check player already joined
            String usernameToCheck = command.playerColor == ChessGame.TeamColor.WHITE ? g.whiteUsername() : g.blackUsername();
            if (usernameToCheck == null) {
                new Error("Error: team empty").send(session);
                return;
            } else if (!usernameToCheck.equals(a.username())) {
                new Error("Error: wrong team").send(session);
                return;
            }

            addClientToGame(command.gameID, session);
            sendLoadGame(session, command.getAuthString(), command.gameID);

            String color = command.playerColor == ChessGame.TeamColor.WHITE ? "white" : "black";
            sendToGameClients(command.gameID, new Notification("Player joined as " + color), session);

        } catch (UnauthorizedException e) {
            new Error("Error: unauthorized").send(session);
        } catch (DataAccessException e) {
            new Error("Error: data access").send(session);
        }
    }

    void joinObserverHandler(Session session, JoinObserver command) throws IOException {
        try {
            GameData g = gameService.getGame(command.getAuthString(), command.gameID);
            addClientToGame(command.gameID, session);
            sendLoadGame(session, command.getAuthString(), command.gameID);
            sendToGameClients(command.gameID, new Notification("Observer joined"), session);
            System.out.println("Joined game as observer");
        } catch (UnauthorizedException e) {
            new Error("Error: unauthorized").send(session);
        }
    }

    void leaveHandler(Session session, Leave command) throws IOException {
        try {
            gameService.leaveGamePlayer(command.getAuthString(), command.gameID);
            // remove from game sessions
            ArrayList<Session> sessions = gameSessions.get(command.gameID);
            if (sessions != null) sessions.remove(session);
            sendToGameClients(command.gameID, new Notification("Player left game"), session);
            System.out.println("User left game");
        } catch (UnauthorizedException e) {
            new Error("Error: unauthorized").send(session);
        }
    }
}
