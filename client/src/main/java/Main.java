import chess.*;
import model.AuthData;
import serverAccess.ServerFacade;
import ui.EscapeSequences;
import ui.UI;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        ServerFacade facade = new ServerFacade(8080);
        UI ui = new UI(facade);
    }
}