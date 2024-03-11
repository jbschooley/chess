package ui;

import model.AuthData;

import java.util.Scanner;

public class UI {
    AuthData auth = null;
    public UI() {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_WHITE + "♕ Welcome to 240 Chess. Type Help to get started. ♕\n");

        while (true) {
            boolean loggedIn = auth != null;
            System.out.printf("[%s] >>> ", auth == null ? "LOGGED_OUT" : "LOGGED_IN");
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            String[] args = line.split(" ");

            if (loggedIn) {
                uiPostLogin(args);
            } else {
                uiPreLogin(args);
            }
        }
    }

    private void uiPreLogin(String[] args) {
        switch (args[0].toLowerCase()) {
            case "help":
                System.out.println(
                        EscapeSequences.SET_TEXT_COLOR_BLUE + "  register <USERNAME> <PASSWORD> <EMAIL>" + EscapeSequences.SET_TEXT_COLOR_MAGENTA + " - to create an account\n" +
                        EscapeSequences.SET_TEXT_COLOR_BLUE + "  login <USERNAME> <PASSWORD>" + EscapeSequences.SET_TEXT_COLOR_MAGENTA + " - to play chess\n" +
                        EscapeSequences.SET_TEXT_COLOR_BLUE + "  quit" + EscapeSequences.SET_TEXT_COLOR_MAGENTA + " - playing chess\n" +
                        EscapeSequences.SET_TEXT_COLOR_BLUE + "  help" + EscapeSequences.SET_TEXT_COLOR_MAGENTA + " - with possible commands\n" +
                        EscapeSequences.SET_TEXT_COLOR_WHITE
                );
                break;
            case "quit":
                System.exit(0);
                break;
            case "register":
                System.out.println("Registering...");
                break;
            case "login":
                System.out.println("Logging in...");
                break;
            default:
                System.out.println("Invalid command. Type Help for a list of commands.");
                break;
        }
    }

    private void uiPostLogin(String[] args) {
    }
}
