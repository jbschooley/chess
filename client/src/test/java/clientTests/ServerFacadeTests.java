package clientTests;

import model.AuthData;
import org.junit.jupiter.api.*;
import server.Server;
import serverAccess.ServerFacade;


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

}
