package webSocketMessages.serverMessages;

import com.google.gson.annotations.Expose;

public class Error extends ServerMessage {

    @Expose
    public final String errorMessage;

    public Error(String errorMessage) {
        super(ServerMessageType.ERROR);
        this.errorMessage = errorMessage;
    }

}
