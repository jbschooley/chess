package ui;

import model.AuthData;
import model.GameData;
import serverAccess.ServerFacade;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Scanner;

import static ui.ChessBoard.drawBoard;

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
                System.out.println("An error occurred: " + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
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
                facade.createGame(auth.authToken(), args[1]);
                System.out.println("Created game " + args[1]);
                break;
            case "list":
                listGames();
                break;
            case "join":
                int gameID = lastGames.get(Integer.parseInt(args[1]));
                String colorString = args[2];
                facade.joinGame(auth.authToken(), gameID, colorString);
                System.out.println("Joined game " + args[1] + " as " + colorString + " player");
                new GameplayUI(facade, gameID, colorString);
//                System.out.println(drawBoard(new ChessGame(), ChessGame.TeamColor.BLACK));
//                System.out.println(drawBoard(new ChessGame(), ChessGame.TeamColor.WHITE));
                break;
            case "observe":
                gameID = lastGames.get(Integer.parseInt(args[1]));
                facade.observeGame(auth.authToken(), gameID);
                System.out.println("Joined game " + args[1] + " as observer");
                new GameplayUI(facade, gameID, null);
//                System.out.println(drawBoard(new ChessGame(), ChessGame.TeamColor.BLACK));
//                System.out.println(drawBoard(new ChessGame(), ChessGame.TeamColor.WHITE));
                break;
            case "logout":
                facade.logout(auth.authToken());
                auth = null;
                break;
            default:
                System.out.println("Invalid command. Type 'help' for a list of commands.");
                break;
        }
    }

    private void listGames() throws Exception {
        Collection<GameData> games = facade.listGames(auth.authToken());
        GameData[] gameArray = games.toArray(new GameData[0]);
        Map<Integer, Integer> thisGames = new java.util.HashMap<>();
        for (int i = 0; i < gameArray.length; i++) {
            String gameString = String.format("%d: %s", i, gameArray[i].gameName());
            if (gameArray[i].whiteUsername() != null) {
                gameString += " (white: " + gameArray[i].whiteUsername() + ")";
            }
            if (gameArray[i].blackUsername() != null) {
                gameString += " (black: " + gameArray[i].blackUsername() + ")";
            }
            System.out.println(gameString);
            thisGames.put(i, gameArray[i].gameID());
        }
        lastGames = thisGames;
    }

    private static String helpLine(String command, String description) {
        return EscapeSequences.SET_TEXT_COLOR_BLUE + "  " + command + EscapeSequences.SET_TEXT_COLOR_MAGENTA + " - " + description + "\n";
    }
}
