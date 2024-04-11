package webSocketMessages.userCommands;

import chess.ChessMove;

public class Leave extends UserGameCommand {
    public final int gameID;

    public Leave(String authToken, int gameID) {
        super(authToken);
        this.commandType = CommandType.LEAVE;
        this.gameID = gameID;
    }
}
