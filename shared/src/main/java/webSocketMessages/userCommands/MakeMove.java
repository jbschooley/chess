package webSocketMessages.userCommands;

import chess.ChessMove;
import com.google.gson.annotations.Expose;

public class MakeMove extends UserGameCommand {

    @Expose
    public final int gameID;

    @Expose
    public final ChessMove move;

    public MakeMove(String authToken, int gameID, ChessMove move) {
        super(authToken);
        this.commandType = CommandType.MAKE_MOVE;
        this.gameID = gameID;
        this.move = move;
    }
}
