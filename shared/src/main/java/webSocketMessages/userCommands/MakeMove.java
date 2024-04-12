package webSocketMessages.userCommands;

import chess.ChessGame;
import chess.ChessMove;

public class MakeMove extends UserGameCommand {
    public final int gameID;
    public final ChessMove move;

    public MakeMove(String authToken, int gameID, ChessMove move) {
        super(authToken);
        this.commandType = CommandType.MAKE_MOVE;
        this.gameID = gameID;
        this.move = move;
    }
}
