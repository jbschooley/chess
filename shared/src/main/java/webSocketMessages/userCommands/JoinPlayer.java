package webSocketMessages.userCommands;

import chess.ChessGame;
import com.google.gson.annotations.Expose;

public class JoinPlayer extends UserGameCommand {

    @Expose
    public final int gameID;

    @Expose
    public final ChessGame.TeamColor playerColor;

    public JoinPlayer(String authToken, int gameID, ChessGame.TeamColor playerColor) {
        super(authToken);
        this.commandType = CommandType.JOIN_PLAYER;
        this.gameID = gameID;
        this.playerColor = playerColor;
    }
}
