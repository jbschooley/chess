package ui;

import chess.ChessGame;
import model.AuthData;
import serverAccess.ServerFacade;
import webSocketMessages.userCommands.JoinObserver;
import webSocketMessages.userCommands.JoinPlayer;
import webSocketMessages.userCommands.Leave;
import webSocketMessages.userCommands.Resign;

import java.util.Scanner;
import javax.websocket.*;

public class GameplayUI extends Endpoint {

    ServerFacade facade;
    AuthData auth;
    int gameID;
    String gameName;
    ChessGame.TeamColor color;
    boolean isPlayer;
    Session ws;

    public GameplayUI(ServerFacade facade, AuthData a, int gameID, String colorString) throws Exception {

        this.facade = facade;
        this.auth = a;
        this.gameID = gameID;
        this.color = colorString != null ? ChessGame.TeamColor.valueOf(colorString) : null;
        this.isPlayer = colorString != null;

        this.ws = facade.websocket(this);
        this.ws.addMessageHandler(messageHandler);

        if (isPlayer) {
            send(new JoinPlayer(this.auth.authToken(), this.gameID, color).toJson());
        } else {
            send(new JoinObserver(this.auth.authToken(), this.gameID).toJson());
        }

        gameLoop: while (true) {
            System.out.printf("%s" + "game >>> ", EscapeSequences.SET_TEXT_COLOR_WHITE);
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            String[] args = line.split(" ");

            try {
                switch (args[0].toLowerCase()) {
                    case "help":
                        System.out.println(
                                helpLine("move <FROM> <TO>", "to move a piece") +
                                helpLine("highlight", "legal moves") +
                                helpLine("redraw", "the board") +
                                helpLine("resign", "playing chess") +
                                helpLine("leave", "the game") +
                                helpLine("help", "with possible commands")
                        );
                        break;
                    case "resign":
                        resign(scanner);
                        break;
                    case "leave":
                        System.out.println("Leaving game...");
                        send(new Leave(this.auth.authToken(), this.gameID).toJson());
                        break gameLoop;
                    case "move":
                        // TODO
                        break;
                    case "highlight":
                        // TODO
                        break;
                    case "redraw":
                        // TODO
                        break;
                    default:
                        System.out.println("Invalid command. Type 'help' for a list of commands.");
                        break;
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }
    }

    private void resign(Scanner scanner) {
        System.out.print("Are you sure you want to resign? Type 'yes' to confirm. >>> ");
        String confirm = scanner.nextLine();
        if (confirm.equals("yes"))
            send(new Resign(this.auth.authToken(), this.gameID).toJson());
    }

    private static String helpLine(String command, String description) {
        return EscapeSequences.SET_TEXT_COLOR_BLUE + "  " + command + EscapeSequences.SET_TEXT_COLOR_MAGENTA + " - " + description + "\n";
    }

    private void send(String message) {
        try {
            ws.getBasicRemote().sendText(message);
        } catch (Exception e) {
            System.out.println("Error sending message: " + e.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        System.out.println("Opening session");
    }

    MessageHandler.Whole<String> messageHandler = new MessageHandler.Whole<String>() {
        @Override
        public void onMessage(String message) {
            System.out.println("Received message: " + message);
        }
    };
}
