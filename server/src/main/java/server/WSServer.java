package server;

import chess.ChessGame;
import dataAccess.*;
import exceptions.AlreadyTakenException;
import exceptions.UnauthorizedException;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import service.ClearService;
import service.GameService;
import service.UserService;
import spark.Spark;
import com.google.gson.Gson;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.userCommands.*;

import java.io.IOException;
import java.util.Objects;

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
    public void onMessage(Session user, String message)  {
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

        // Handle the command
        try {
            switch (Objects.requireNonNull(c).getCommandType()) {
                case JOIN_PLAYER -> joinGameHandler(user, (JoinPlayer) c);
                case JOIN_OBSERVER -> System.out.println("Join observer");
                case MAKE_MOVE -> System.out.println("Make move");
                case LEAVE -> System.out.println("Leave");
                case RESIGN -> System.out.println("Resign");
                default -> System.out.println("Unknown command type: " + c.getCommandType());
            }
        } catch (IOException e) {
            System.out.println("Error handling command: " + e.getMessage());
            e.printStackTrace();
        }
    }

    void joinGameHandler(Session user, JoinPlayer command) throws IOException {
        if (command.getAuthString() == null) {
            System.out.println("No auth token provided");
            return;
        }
        try {
            switch (command.playerColor) {
                case WHITE -> {
                    gameService.joinGamePlayer(command.getAuthString(), command.gameID, ChessGame.TeamColor.WHITE);
                    System.out.println("Joined game as white");
                }
                case BLACK -> {
                    gameService.joinGamePlayer(command.getAuthString(), command.gameID, ChessGame.TeamColor.BLACK);
                    System.out.println("Joined game as black");
                }
                default -> {
                    new Error("Error: invalid player color").send(user);
                }
            }
        } catch (UnauthorizedException e) {
            new Error("Error: unauthorized").send(user);
        } catch (AlreadyTakenException e) {
            new Error("Error: already taken").send(user);
        }
    }
}
