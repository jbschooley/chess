package ui;

import model.AuthData;
import model.GameData;
import serverAccess.ServerFacade;

import java.util.Collection;
import java.util.Map;
import java.util.Scanner;

public class UI {
    AuthData auth = null;
    ServerFacade facade;
    Map<Integer, Integer> lastGames;

    public UI(ServerFacade facade) {
        this.facade = facade;
        System.out.println(EscapeSequences.SET_TEXT_COLOR_WHITE + "♕ Welcome to 240 Chess. Type Help to get started. ♕\n");

        while (true) {
            boolean loggedIn = auth != null;
            System.out.printf("%s[%s] >>> ", EscapeSequences.SET_TEXT_COLOR_WHITE, auth == null ? "LOGGED_OUT" : "LOGGED_IN");
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            String[] args = line.split(" ");

            try {
                if (loggedIn) {
                    uiPostLogin(args);
                } else {
                    uiPreLogin(args);
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }
    }

    private void uiPreLogin(String[] args) throws Exception {
        switch (args[0].toLowerCase()) {
            case "help":
                System.out.println(
                        helpLine("register <USERNAME> <PASSWORD> <EMAIL>", "to create an account") +
                        helpLine("login <USERNAME> <PASSWORD>", "to play chess") +
                        helpLine("quit", "playing chess") +
                        helpLine("help", "with possible commands")
                );
                break;
            case "quit":
                System.exit(0);
                break;
            case "register":
                auth = facade.register(args[1], args[2], args[3]);
                System.out.println("Registered as " + auth.username());
                break;
            case "login":
                auth = facade.login(args[1], args[2]);
                System.out.println("Logged in as " + auth.username());
                break;
            default:
                System.out.println("Invalid command. Type Help for a list of commands.");
                break;
        }
    }

    private void uiPostLogin(String[] args) throws Exception {
        switch (args[0].toLowerCase()) {
            case "help":
                System.out.println(
                        helpLine("create <NAME>", "a game") +
                        helpLine("list", "games") +
                        helpLine("join <ID> [WHITE|BLACK|<empty>]", "a game") +
                        helpLine("observe <ID>", "a game") +
                        helpLine("logout", "when you are done") +
                        helpLine("quit", "playing chess") +
                        helpLine("help", "with possible commands")
                );
                break;
            case "quit":
                System.exit(0);
                break;
            case "create":
                System.out.println("Creating game...");
                // TODO: create
                break;
            case "list":
                System.out.println("Listing games...");
                listGames();
                break;
            case "join":
                System.out.println("Joining game...");
                // TODO: join
                break;
            case "observe":
                System.out.println("Observing game...");
                // TODO: observe
                break;
            case "logout":
                facade.logout(auth.authToken());
                auth = null;
                break;
            default:
                System.out.println("Invalid command. Type Help for a list of commands.");
                break;
        }
    }

    private void listGames() throws Exception {
        Collection<GameData> games = facade.listGames(auth.authToken());
        for (GameData game : games) {
            System.out.println(game);
        }
        // TODO save ids and numbers to lastGames
    }

    private static String helpLine(String command, String description) {
        return EscapeSequences.SET_TEXT_COLOR_BLUE + "  " + command + EscapeSequences.SET_TEXT_COLOR_MAGENTA + " - " + description + "\n";
    }
}
