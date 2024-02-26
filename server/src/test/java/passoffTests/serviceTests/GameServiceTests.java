package passoffTests.serviceTests;

import dataAccess.*;
import exceptions.UnauthorizedException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import passoffTests.testClasses.TestException;
import service.ClearService;
import service.GameService;
import service.UserService;

public class GameServiceTests {

    // DAOs
    GameDAO gameDao = new MemoryGameDAO();
    AuthDAO authDao = new MemoryAuthDAO();
    UserDAO userDao = new MemoryUserDAO();

    ClearService clearService = new ClearService(gameDao, authDao, userDao);
    UserService userService = new UserService(authDao, userDao);
    GameService gameService = new GameService(authDao, userDao, gameDao);

    // test user
    String testUserUsername = "testuser";
    String testUserPassword = "testpass";
    String testUserEmail = "a@b.c";

    AuthData auth;

    @BeforeEach
    public void setup() throws TestException, DataAccessException, UnauthorizedException {
        clearService.clear();

        // create test user
        UserData u = new UserData(testUserUsername, testUserPassword, testUserEmail);
        userDao.createUser(u);

        // login
        auth = userService.login(testUserUsername, testUserPassword);
    }

    @Test
    @DisplayName("List Games")
    public void listGames() throws TestException, DataAccessException, UnauthorizedException {
        // create 2 games
        gameDao.createGame("game1");
        gameDao.createGame("game2");

        // verify 2 games are returned by listGames
        Assertions.assertEquals(gameService.listGames(auth.authToken()).size(), 2);

    }

    @Test
    @DisplayName("List Games Unauthorized")
    public void listGamesUnauthorized() throws TestException {
        // verify it checks auth
        Assertions.assertThrows(UnauthorizedException.class, () -> gameService.listGames("wrong"));

    }

    @Test
    @DisplayName("Create Game")
    public void createGame() throws TestException, UnauthorizedException {
        gameService.createGame(auth.authToken(), "testgame");

        // verify game is in listGames
        Assertions.assertEquals(gameService.listGames(auth.authToken()).size(), 1);
    }

    @Test
    @DisplayName("Create Game Unauthorized")
    public void createGameUnauthorized() throws TestException {
        // verify it checks auth
        Assertions.assertThrows(UnauthorizedException.class, () -> gameService.createGame("wrong", "nope"));

    }
}
