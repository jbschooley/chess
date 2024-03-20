package clientTests;

import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.*;
import server.Server;
import serverAccess.ServerFacade;

import java.util.Collection;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    String username = "testuser";
    String password = "testpass";
    String email = "a@b.c";
    AuthData a;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @BeforeEach
    public void clear() throws Exception {
        facade.clear();
        // register a user
        a = facade.register(username, password, email);

    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void register() {
        AuthData a1 = Assertions.assertDoesNotThrow(() -> facade.register("testuser1", password, email));
        Assertions.assertEquals("testuser1", a1.username());
        Assertions.assertNotNull(a1.authToken());
    }

    @Test
    public void registerFailDuplicate() {
        Assertions.assertThrows(Exception.class, () -> facade.register(username, password, email));
    }

    @Test
    public void login() {
        AuthData a1 = Assertions.assertDoesNotThrow(() -> facade.login(username, password));
        Assertions.assertEquals(username, a1.username());
        Assertions.assertNotNull(a1.authToken());
    }

    @Test
    public void loginFailInvalid() {
        Assertions.assertThrows(Exception.class, () -> facade.login(username, "wrongpassword"));
    }

    @Test
    public void logout() {
        Assertions.assertDoesNotThrow(() -> facade.logout(a.authToken()));
    }

    @Test
    public void logoutFailInvalid() {
        Assertions.assertThrows(Exception.class, () -> facade.logout("invalidtoken"));
    }

    @Test
    public void createGame() {
        GameData g = Assertions.assertDoesNotThrow(() -> facade.createGame(a.authToken(), "testgame"));
        Assertions.assertEquals("testgame", g.gameName());
    }

    @Test
    public void createGameFail() {
        Assertions.assertThrows(Exception.class, () -> facade.createGame(a.authToken(), null));
    }

    @Test
    public void listGames() {
        // create game
        GameData g = Assertions.assertDoesNotThrow(() -> facade.createGame(a.authToken(), "testgame"));
        Assertions.assertEquals("testgame", g.gameName());

        // list games
        Collection<GameData> games = Assertions.assertDoesNotThrow(() -> facade.listGames(a.authToken()));
        Assertions.assertEquals(1, games.size());
        GameData g1 = games.iterator().next();
        Assertions.assertEquals(g, g1);
    }

    @Test
    public void listGamesUnauthorized() {
        Assertions.assertThrows(Exception.class, () -> facade.listGames("invalidtoken"));
    }

}
