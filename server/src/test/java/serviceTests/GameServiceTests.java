package serviceTests;

import chess.ChessGame;
import dataAccess.*;
import exceptions.AlreadyTakenException;
import exceptions.UnauthorizedException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import passoffTests.testClasses.TestException;
import service.ClearService;
import service.GameService;
import service.UserService;

public class GameServiceTests {

    // DAOs
    GameDAO gameDao;
    AuthDAO authDao;
    UserDAO userDao;

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
        // Initialize DAOs and services
        try {
            userDao = new SqlUserDAO();
            authDao = new SqlAuthDAO();
            gameDao = new SqlGameDAO();

            clearService = new ClearService(gameDao, authDao, userDao);
            userService = new UserService(authDao, userDao);
            gameService = new GameService(authDao, userDao, gameDao);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

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

    @Test
    @DisplayName("Get Game")
    public void getGame() throws TestException, UnauthorizedException {
        String name = "testgame";

        int gameID = gameService.createGame(auth.authToken(), name);
        GameData g = gameService.getGame(auth.authToken(), gameID);
        Assertions.assertEquals(g.gameName(), name);
    }

    @Test
    @DisplayName("Get Game Unauthorized")
    public void getGameUnauthorized() throws TestException {
        // verify it checks auth
        Assertions.assertThrows(UnauthorizedException.class, () -> gameService.getGame("wrong", 123));
    }

    @Test
    @DisplayName("Join Game as Player")
    public void joinGamePlayer() throws TestException, UnauthorizedException, AlreadyTakenException {
        String name = "testgame";
        int gameID = gameService.createGame(auth.authToken(), name);

        gameService.joinGamePlayer(auth.authToken(), gameID, ChessGame.TeamColor.WHITE);
        GameData g = gameService.getGame(auth.authToken(), gameID);
        Assertions.assertEquals(g.whiteUsername(), testUserUsername);
    }

    @Test
    @DisplayName("Join Game Already Taken")
    public void joinGamePlayerAlreadyTaken() throws TestException, UnauthorizedException, AlreadyTakenException {
        String name = "testgame";
        int gameID = gameService.createGame(auth.authToken(), name);

        // join game
        gameService.joinGamePlayer(auth.authToken(), gameID, ChessGame.TeamColor.WHITE);

        // join game again
        Assertions.assertThrows(AlreadyTakenException.class, () -> gameService.joinGamePlayer(auth.authToken(), gameID, ChessGame.TeamColor.WHITE));
    }


}
