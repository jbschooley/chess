package webSocketMessages.serverMessages;

import chess.ChessGame;
import com.google.gson.annotations.Expose;

public class Notification extends ServerMessage {

    @Expose
    public final String message;

    public Notification(String message) {
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }

}
