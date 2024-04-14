package ui;

import serverAccess.ServerFacade;
import java.util.Scanner;

public class GameplayUI {

    ServerFacade facade;
    int gameID;
    String gameName;
    boolean isPlayer;

    public GameplayUI(ServerFacade facade, int gameID, String colorString) {
        this.facade = facade;

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
}
