package webSocketMessages.serverMessages;

import chess.ChessGame;
import com.google.gson.annotations.Expose;

public class LoadGame extends ServerMessage {

    @Expose
    private final ChessGame game;

    public LoadGame(ChessGame game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

}
