package serviceTests;

import dataAccess.*;
import exceptions.UnauthorizedException;
import model.UserData;
import org.junit.jupiter.api.*;
import passoffTests.testClasses.TestException;
import server.Server;
import service.ClearService;
import service.UserService;

public class ClearServiceTests {

    // DAOs
    GameDAO gameDao;
    AuthDAO authDao;
    UserDAO userDao;

    ClearService clearService = new ClearService(gameDao, authDao, userDao);

    @Test
    @DisplayName("Clear Method")
    public void testClear() throws TestException, DataAccessException, UnauthorizedException {

        // Initialize DAOs and services
        try {
            userDao = new SqlUserDAO();
            authDao = new SqlAuthDAO();
            gameDao = new SqlGameDAO();

            clearService = new ClearService(gameDao, authDao, userDao);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        // clear
        clearService.clear();

        UserData tu = new UserData("testuser", "testpass", "a@b.c");

        // create filler user
        userDao.createUser(tu);

        // create filler game
        gameDao.createGame("testgame");

        // test user exists
        Assertions.assertEquals(userDao.getUser("testuser").username(), tu.username());

        // clear
        clearService.clear();

        // test that user no longer exist
        Assertions.assertThrows(UnauthorizedException.class, () -> userDao.getUser("testuser"));
    }
}
