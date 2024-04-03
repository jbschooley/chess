package webSocketMessages.serverMessages;

import chess.ChessGame;

public class Error extends ServerMessage {
    private final String errorMessage;

    public Error(String errorMessage) {
        super(ServerMessageType.ERROR);
        this.errorMessage = errorMessage;
    }

}
