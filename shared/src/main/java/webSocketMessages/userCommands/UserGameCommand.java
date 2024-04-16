package webSocketMessages.userCommands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import java.util.Objects;

/**
 * Represents a command a user can send the server over a websocket
 * 
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */

public class UserGameCommand {

    public UserGameCommand(String authToken) {
        this.authToken = authToken;
    }

    public enum CommandType {
        JOIN_PLAYER,
        JOIN_OBSERVER,
        MAKE_MOVE,
        LEAVE,
        RESIGN
    }

    @Expose
    protected CommandType commandType;

    @Expose
    private final String authToken;

    public String getAuthString() {
        return authToken;
    }

    public CommandType getCommandType() {
        return this.commandType;
    }

    public String toJson() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof UserGameCommand))
            return false;
        UserGameCommand that = (UserGameCommand) o;
        return getCommandType() == that.getCommandType() && Objects.equals(getAuthString(), that.getAuthString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCommandType(), getAuthString());
    }

    public static UserGameCommand fromJson(String message) {
        Gson gson = new Gson();
        UserGameCommand c = gson.fromJson(message, UserGameCommand.class);

        return gson.fromJson(message, switch (c.getCommandType()) {
            case JOIN_PLAYER -> JoinPlayer.class;
            case JOIN_OBSERVER -> JoinObserver.class;
            case MAKE_MOVE -> MakeMove.class;
            case LEAVE -> Leave.class;
            case RESIGN -> Resign.class;
        });
    }
}
