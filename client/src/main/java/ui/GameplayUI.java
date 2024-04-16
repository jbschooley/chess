package ui;

import model.AuthData;
import serverAccess.ServerFacade;
import webSocketMessages.userCommands.Leave;

import java.util.Scanner;
import javax.websocket.*;

public class GameplayUI extends Endpoint {

    ServerFacade facade;
    AuthData auth;
    int gameID;
    String gameName;
    String colorString;
    boolean isPlayer;
    Session ws;

    public GameplayUI(ServerFacade facade, AuthData a, int gameID, String colorString) throws Exception {

        this.facade = facade;
        this.auth = a;
        this.gameID = gameID;
        this.colorString = colorString;
        this.isPlayer = colorString != null;

        this.ws = facade.websocket(this);
        this.ws.addMessageHandler(messageHandler);

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
                    case "leave":
                        System.out.println("Leaving game...");
                        send(new Leave(this.auth.authToken(), this.gameID).toJson());
                        break gameLoop;
                    case "move":
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
