package webSocketMessages.userCommands;

import com.google.gson.annotations.Expose;

public class JoinObserver extends UserGameCommand {

    @Expose
    public final int gameID;

    public JoinObserver(String authToken, int gameID) {
        super(authToken);
        this.commandType = CommandType.JOIN_OBSERVER;
        this.gameID = gameID;
    }
}
