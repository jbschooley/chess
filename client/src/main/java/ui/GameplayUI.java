package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import model.AuthData;
import serverAccess.ServerFacade;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;

import java.util.Collection;
import java.util.Scanner;
import javax.websocket.*;

import static ui.ChessBoard.drawBoard;

public class GameplayUI extends Endpoint {

    ServerFacade facade;
    AuthData auth;
    int gameID;
    String gameName;
    ChessGame.TeamColor color;
    boolean isPlayer;
    Session ws;
    boolean isPrompting = false;
    ChessGame game;

    public GameplayUI(ServerFacade facade, AuthData a, int gameID, String colorString) throws Exception {

        this.facade = facade;
        this.auth = a;
        this.gameID = gameID;
        this.color = colorString != null ? ChessGame.TeamColor.valueOf(colorString) : ChessGame.TeamColor.WHITE;
        this.isPlayer = colorString != null;

        this.ws = facade.websocket(this);
        this.ws.addMessageHandler(messageHandler);

        if (isPlayer) {
            send(new JoinPlayer(this.auth.authToken(), this.gameID, color).toJson());
        } else {
            send(new JoinObserver(this.auth.authToken(), this.gameID).toJson());
        }

        gameLoop: while (true) {
            printPrompt();
            Scanner scanner = new Scanner(System.in);
            isPrompting = true;
            String line = scanner.nextLine();
            isPrompting = false;
            String[] args = line.split(" ");

            try {
                switch (args[0].toLowerCase()) {
                    case "help" -> {
                        System.out.println(
                                helpLine("move <FROM> <TO>", "to move a piece") +
                                helpLine("valid <FROM>", "highlight legal moves") +
                                helpLine("redraw", "the board") +
                                helpLine("resign", "playing chess") +
                                helpLine("leave", "the game") +
                                helpLine("help", "with possible commands")
                        );
                    }
                    case "resign" -> {
                        resign(scanner);
                    }
                    case "leave" -> {
                        System.out.println("Leaving game...");
                        send(new Leave(this.auth.authToken(), this.gameID).toJson());
                        break gameLoop;
                    }
                    case "move" -> {
                        move(args);
                    }
                    case "valid" -> {
                        validMoves(args);
                    }
                    case "redraw" -> {
                        System.out.println("\n" + drawBoard(game, color));
                    }
                    default -> {
                        System.out.println("Invalid command. Type 'help' for a list of commands.");
                    }
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }
    }

    private void move(String[] args) {
        if (args.length != 3) {
            System.out.println("Invalid move command. Type 'help' for a list of commands.");
            return;
        }
        String from = args[1];
        String to = args[2];

        // parse from and to

        ChessPosition fromPos = new ChessPosition(from.charAt(1) - '0', from.charAt(0) - '`');
        ChessPosition toPos = new ChessPosition(to.charAt(1) - '0', to.charAt(0) - '`');
        ChessMove move = new ChessMove(fromPos, toPos, null);

        send(new MakeMove(this.auth.authToken(), this.gameID, move).toJson());
    }

    private void validMoves(String[] args) {
        if (args.length != 2) {
            System.out.println("Invalid move command. Type 'help' for a list of commands.");
            return;
        }
        String from = args[1];

        // parse from
        ChessPosition fromPos = new ChessPosition(from.charAt(1) - '0', from.charAt(0) - '`');

        Collection<ChessMove> moves = game.validMoves(fromPos);
        System.out.println("\n" + drawBoard(game, color, fromPos, moves));
    }

    private void resign(Scanner scanner) {
        System.out.print("Are you sure you want to resign? Type 'yes' to confirm. >>> ");
        isPrompting = true;
        String confirm = scanner.nextLine();
        isPrompting = false;
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

    private void printPrompt() {
        System.out.printf("%s" + ">>> ", EscapeSequences.RESET);
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        System.out.println("Opening session");
    }

    MessageHandler.Whole<String> messageHandler = new MessageHandler.Whole<String>() {
        @Override
        public void onMessage(String message) {
            ServerMessage m = ServerMessage.fromJson(message);

            switch (m.getServerMessageType()) {
                case LOAD_GAME -> {
                    LoadGame lg = (LoadGame) m;
                    game = lg.game;
                    System.out.println("\n" + drawBoard(game, color));
                    if (isPrompting) printPrompt();
                }
                case ERROR -> {
                    Error e = (Error) m;
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + e.errorMessage + "\n");
                    if (isPrompting) printPrompt();
                }
                case NOTIFICATION -> {
                    Notification n = (Notification) m;
                    System.out.println("\nNotification: " + n.message + "\n");
                    if (isPrompting) printPrompt();
                }
            }
        }
    };
}
