package webSocketMessages.serverMessages;

import chess.ChessGame;

public class Notification extends ServerMessage {
    private final String message;

    public Notification(String message) {
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }

}
