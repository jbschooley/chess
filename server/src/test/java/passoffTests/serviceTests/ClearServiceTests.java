package passoffTests.serviceTests;

import dataAccess.*;
import model.UserData;
import org.junit.jupiter.api.*;
import passoffTests.testClasses.TestException;
import server.Server;
import service.ClearService;

public class ClearServiceTests {

    // DAOs
    GameDAO gameDao = new MemoryGameDAO();
    AuthDAO authDao = new MemoryAuthDAO();
    UserDAO userDao = new MemoryUserDAO();

    ClearService clearService = new ClearService(gameDao, authDao, userDao);

    @Test
    @DisplayName("Clear Method")
    public void testClear() throws TestException, DataAccessException {

        UserData tu = new UserData("testuser", "testpass", "a@b.c");

        // create filler user
        userDao.createUser(tu);

        // create filler game
        gameDao.createGame("testgame");

        // test user exists
        Assertions.assertEquals(userDao.getUser("testuser"), tu);

        // clear
        clearService.clear();

        // test that user no longer exist
        Assertions.assertThrows(DataAccessException.class, () -> userDao.getUser("testuser"));
    }
}
