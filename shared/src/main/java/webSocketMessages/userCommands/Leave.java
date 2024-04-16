package webSocketMessages.userCommands;

import com.google.gson.annotations.Expose;

public class Leave extends UserGameCommand {

    @Expose
    public final int gameID;

    public Leave(String authToken, int gameID) {
        super(authToken);
        this.commandType = CommandType.LEAVE;
        this.gameID = gameID;
    }
}
