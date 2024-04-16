package webSocketMessages.userCommands;

import com.google.gson.annotations.Expose;

public class Resign extends UserGameCommand {

    @Expose
    public final int gameID;

    public Resign(String authToken, int gameID) {
        super(authToken);
        this.commandType = CommandType.RESIGN;
        this.gameID = gameID;
    }
}
